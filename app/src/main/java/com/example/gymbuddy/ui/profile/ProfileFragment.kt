package com.example.gymbuddy.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.gymbuddy.Objects.GalleryHandler
import com.example.gymbuddy.ui.dialog.EditDisplayNameDialogFragment
import com.example.gymbuddy.ui.dialog.EditProfileImageDialogFragment
import com.example.gymbuddy.ViewModels.AuthViewModel
import com.example.gymbuddy.ViewModels.UserViewModel
import com.example.gymbuddy.R
import com.example.gymbuddy.dataclass.User
import com.example.gymbuddy.Objects.GlobalVariables
import com.squareup.picasso.Picasso

private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1
private const val PICK_IMAGE_REQUEST_CODE = 2

class ProfileFragment : Fragment(),
    EditDisplayNameDialogFragment.EditUsernameDialogListener {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var userViewModel: UserViewModel
    private lateinit var sharedPreferences: SharedPreferences

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                GalleryHandler.getPhotoUriFromGallery(requireActivity(), pickImageLauncher, null)
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                // Handle the selected image URI
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {
                    // Use the selectedImageUri as needed
                    // For example, update the UI with the selected image
                    userViewModel.updateUserPhoto(selectedImageUri)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        GlobalVariables.currentUser?.let { user ->
            updateUI(user, view)
        }

        authViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                val emailTextView: TextView = view.findViewById(R.id.emailTextView)
                emailTextView.text = user.email
                // Initialize or update UserViewModel when the current user is available
                userViewModel = UserViewModel(user.uid)
                userViewModel.userLiveData.observe(viewLifecycleOwner, Observer { userData ->
                    userData?.let {
                        if (userData != GlobalVariables.currentUser) {
                            GlobalVariables.currentUser = userData
                            updateUI(userData)
                        }
                    }
                })
            }
        })
        val displayNameTextView: TextView = view.findViewById(R.id.displayNameTextView)
        val userPhotoImageView: ImageView = view.findViewById(R.id.userPhotoImageView)

        displayNameTextView.setOnClickListener {
            showEditUsernameDialog()
        }
        userPhotoImageView.setOnClickListener {
            Log.d("ProfileFragment", "Profile image clicked")
            GalleryHandler.getPhotoUriFromGallery(requireActivity(), pickImageLauncher, requestPermissionLauncher)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userPhotoImageView: ImageView = view.findViewById(R.id.userPhotoImageView)

        userPhotoImageView.setOnClickListener {
            Log.d("ProfileFragment", "Profile imagdddde clicked")
            GalleryHandler.getPhotoUriFromGallery(requireActivity(), pickImageLauncher, requestPermissionLauncher)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GalleryHandler.REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            selectedImageUri?.let {
                userViewModel.updateUserPhoto(it)
            }
        }
    }

    private fun showEditUsernameDialog() {
        val dialogFragment = EditDisplayNameDialogFragment()
        dialogFragment.show(childFragmentManager, "EditUsernameDialogFragment")
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }

    override fun onDisplayNameUpdated(displayName: String) {
        userViewModel.updateUserName(displayName)
        Log.d("NameUpdate", "Updated display name")
    }

    private fun updateUI(userData: User, thisView: View? = view) {
        val displayNameTextView: TextView? = thisView?.findViewById(R.id.displayNameTextView)
        val emailTextView: TextView? = thisView?.findViewById(R.id.emailTextView)
        val userPhotoImageView: ImageView? = thisView?.findViewById(R.id.userPhotoImageView)

        displayNameTextView?.text = userData.name
        emailTextView?.text = authViewModel.currentUser.value?.email
        // Load user photo using Picasso if available
        userData.photoUrl?.takeIf { it.isNotEmpty() }?.let { url ->
            Picasso.get()
                .load(url)
                .placeholder(R.drawable.trainer_icon)
                .error(R.drawable.trainer_icon)
                .into(userPhotoImageView)
        }
    }
}
