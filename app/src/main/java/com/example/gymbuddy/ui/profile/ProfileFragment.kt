package com.example.gymbuddy.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gymbuddy.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.gymbuddy.ui.dialog.EditDisplayNameDialogFragment
import com.example.gymbuddy.ui.dialog.EditProfileImageDialogFragment
import com.example.gymbuddy.ViewModels.AuthViewModel
import com.example.gymbuddy.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment(),
    EditDisplayNameDialogFragment.EditUsernameDialogListener,
    EditProfileImageDialogFragment.EditProfileImageDialogListener {

    private var param1: String? = null
    private var param2: String? = null
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val displayNameTextView: TextView = view.findViewById(R.id.displayNameTextView)
        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        val userPhotoImageView: ImageView = view.findViewById(R.id.userPhotoImageView)

        sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val displayName = sharedPreferences.getString("displayName", "")
        val email = sharedPreferences.getString("email", "")
        val photoUrl = sharedPreferences.getString("photoUrl", "")

        displayNameTextView.text = displayName
        emailTextView.text = email
        userPhotoImageView.setImageURI(photoUrl?.toUri())

        displayNameTextView.setOnClickListener {
            showEditUsernameDialog()
        }
        userPhotoImageView.setOnClickListener {
            showEditProfileImageDialog()
        }

        return view
    }

    private fun showEditUsernameDialog() {
        val dialogFragment = EditDisplayNameDialogFragment()
        dialogFragment.show(childFragmentManager, "EditUsernameDialogFragment")
    }

    private fun showEditProfileImageDialog() {
        val dialogFragment = EditProfileImageDialogFragment()
        dialogFragment.show(childFragmentManager, "EditProfileImageDialogFragment")
    }

    override fun onDisplayNameUpdated(displayName: String) {
        authViewModel.updateDisplayName(displayName)

        // Update the display name in SharedPreferences
        sharedPreferences.edit {
            putString("displayName", displayName)
        }

        val displayNameTextView: TextView? = view?.findViewById(R.id.displayNameTextView)
        displayNameTextView?.text = displayName
        Log.d("NameUpdate", "Updated display name")
    }

    override fun onImageUpdated(imageUri: String) {
        authViewModel.updateUserPhoto(imageUri.toUri())

        // Update the photo URI in SharedPreferences
        sharedPreferences.edit {
            putString("photoUrl", imageUri)
        }

        val userPhotoImageView: ImageView? = view?.findViewById(R.id.userPhotoImageView)
        userPhotoImageView?.setImageURI(imageUri.toUri())
        Log.d("ImageUpdate", "Updated profile image")
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}