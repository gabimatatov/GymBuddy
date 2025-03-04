package com.example.gymbuddy.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.gymbuddy.R
import com.example.gymbuddy.activities.AuthViewModel
import com.example.gymbuddy.dataclass.User
import com.example.gymbuddy.ui.dialog.EditDisplayNameDialogFragment
import com.example.gymbuddy.objects.CameraUtil
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment(), EditDisplayNameDialogFragment.EditUsernameDialogListener,
    CameraUtil.CameraResultCallback {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userPhotoImageView: ImageView
    private lateinit var cameraUtil: CameraUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize camera utility
        cameraUtil = CameraUtil(this)
        cameraUtil.setCameraResultCallback(this)

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
            cameraUtil.checkCameraPermission()
        }

        authViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                emailTextView.text = user.email
                displayNameTextView.text = user.displayName
                profileViewModel = ProfileViewModel(user.uid)

                profileViewModel.userLiveData.observe(viewLifecycleOwner, Observer { userData ->
                    userData?.let { updateUI(userData) }
                })
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        cameraUtil.processActivityResult(requestCode, resultCode, data)
    }

    override fun onImageCaptured(bitmap: Bitmap) {
        uploadImageToFirebase(bitmap)
    }

    // Upload the Bitmap to Firebase
    private fun uploadImageToFirebase(bitmap: Bitmap) {
        profileViewModel.updateUserPhoto(bitmap)
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
            profileViewModel.updateUserName(displayName)
            Log.d("NameUpdate", "Updated display name")
            Toast.makeText(requireContext(), "Username Updated!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Invalid username!", Toast.LENGTH_SHORT).show()
        }
    }

    // Delete Confirmation
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Profile Photo")
            .setMessage("Are you sure you want to delete your profile photo?")
            .setPositiveButton("Delete") { _, _ -> profileViewModel.deleteUserPhoto() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}