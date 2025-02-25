package com.example.gymbuddy.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gymbuddy.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fetchUserDetails() // Fetch user data from FirebaseAuth

        return root
    }

    private fun fetchUserDetails() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            binding.usernameTextView.text = user.displayName ?: "No Name"
            binding.emailTextView.text = user.email ?: "No Email"

            // Load profile picture using Picasso (if user has one)
            user.photoUrl?.let { profileUri ->
                Picasso.get()
                    .load(profileUri)
                    .placeholder(com.example.gymbuddy.R.drawable.gym_buddy_icon)
                    .error(com.example.gymbuddy.R.drawable.gym_buddy_icon)
                    .into(binding.userPhotoImageView)
            }
        } else {
            // Handle the case where no user is logged in
            binding.usernameTextView.text = "Guest"
            binding.emailTextView.text = "Not logged in"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
