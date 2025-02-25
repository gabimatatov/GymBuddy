package com.example.gymbuddy.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.net.Uri
import android.widget.Toast
import com.example.gymbuddy.repos.FirebaseRepository
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    private val firebaseRepository: FirebaseRepository = FirebaseRepository()
    val currentUser: LiveData<FirebaseUser?> = _currentUser
    val isUserSignedIn: LiveData<Boolean> = currentUser.map { it != null }

    init {
        // Set up a Firebase AuthStateListener to update LiveData on authentication state change
        firebaseRepository.getInstance().addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun signIn(email: String, password: String, context: Context) {
        firebaseRepository.signIn(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in success
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signUp(email: String, password: String, context: Context) {
        firebaseRepository.signUp(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-up success
                } else {
                    // If sign up fails, display a message to the user.
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signOut() {
        firebaseRepository.signOut()
    }

}