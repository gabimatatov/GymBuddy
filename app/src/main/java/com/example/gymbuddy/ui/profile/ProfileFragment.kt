package com.example.gymbuddy.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.gymbuddy.R
import com.example.gymbuddy.activities.AuthViewModel
import com.example.gymbuddy.dataclass.User
import com.example.gymbuddy.ui.dialog.EditDisplayNameDialogFragment
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment(), EditDisplayNameDialogFragment.EditUsernameDialogListener {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var userViewModel: UserViewModel
    private lateinit var userPhotoImageView: ImageView

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayNameTextView: TextView = view.findViewById(R.id.displayNameTextView)
        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        val deleteImageButton: Button = view.findViewById(R.id.deleteImageButton)
        userPhotoImageView = view.findViewById(R.id.userPhotoImageView)

        deleteImageButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }


        displayNameTextView.setOnClickListener {
            val dialogFragment = EditDisplayNameDialogFragment()
            dialogFragment.show(childFragmentManager, "EditDisplayNameDialogFragment")
        }

        userPhotoImageView.setOnClickListener {
            checkCameraPermission()
        }

        authViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                emailTextView.text = user.email
                displayNameTextView.text = user.displayName
                userViewModel = UserViewModel(user.uid)

                userViewModel.userLiveData.observe(viewLifecycleOwner, Observer { userData ->
                    userData?.let { updateUI(userData) }
                })
            }
        })
    }

    // Check for camera permission before opening the camera
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    // Handle the permission result
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val photo = data?.extras?.get("data") as Bitmap?
            photo?.let {
                // Pass the Bitmap directly to the ViewModel
                uploadImageToFirebase(it)
            }
        }
    }

    // Upload the Bitmap to Firebase
    private fun uploadImageToFirebase(bitmap: Bitmap) {
        userViewModel.updateUserPhoto(bitmap)
        Toast.makeText(requireContext(), "Photo Updated", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(userData: User) {
        val displayNameTextView: TextView? = view?.findViewById(R.id.displayNameTextView)
        val emailTextView: TextView? = view?.findViewById(R.id.emailTextView)

        displayNameTextView?.text = userData.name.ifEmpty { "No Name" }
        emailTextView?.text = authViewModel.currentUser.value?.email

        val photoUrl = userData.photoUrl.takeIf { it.isNotEmpty() }
        if (photoUrl != null) {
            Picasso.get().load(photoUrl).into(userPhotoImageView)
        } else {
            userPhotoImageView.setImageResource(R.drawable.trainer_icon)
        }
    }

    override fun onDisplayNameUpdated(displayName: String) {
        if (displayName.isNotEmpty()) {
            userViewModel.updateUserName(displayName)
            Log.d("NameUpdate", "Updated display name")
            Toast.makeText(requireContext(), "Username Updated!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Invalid username!", Toast.LENGTH_SHORT).show()
        }
    }

    // Delete Confirmation
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Workout")
            .setMessage("Are you sure you want to delete this workout?")
            .setPositiveButton("Delete") { _, _ -> userViewModel.deleteUserPhoto() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
