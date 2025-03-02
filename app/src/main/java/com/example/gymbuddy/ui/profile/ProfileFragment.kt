package com.example.gymbuddy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.gymbuddy.R
import com.example.gymbuddy.activities.AuthViewModel
import com.example.gymbuddy.dataclass.User
import com.example.gymbuddy.objects.GlobalVariables
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var userViewModel: UserViewModel

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
        val userPhotoImageView: ImageView = view.findViewById(R.id.userPhotoImageView)

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

    private fun updateUI(userData: User) {
        val displayNameTextView: TextView? = view?.findViewById(R.id.displayNameTextView)
        val emailTextView: TextView? = view?.findViewById(R.id.emailTextView)
        val userPhotoImageView: ImageView? = view?.findViewById(R.id.userPhotoImageView)

        displayNameTextView?.text = userData.name.ifEmpty { "No Name" }
        emailTextView?.text = authViewModel.currentUser.value?.email

        // Load user photo using Picasso, show default if empty
        val photoUrl = userData.photoUrl.takeIf { it.isNotEmpty() }
        if (photoUrl != null) {
            Picasso.get()
                .load(photoUrl)
                .into(userPhotoImageView)
        } else {
            userPhotoImageView?.setImageResource(R.drawable.trainer_icon)
        }
    }
}
