package com.example.gymbuddy.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.example.gymbuddy.repos.FirebaseRepository
import androidx.lifecycle.map
import com.example.gymbuddy.activities.LoginActivity
import com.example.gymbuddy.activities.SignupActivity

class AuthViewModel : ViewModel() {

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser
    val isUserSignedIn: LiveData<Boolean> = currentUser.map { it != null }

    private val firebaseRepository: FirebaseRepository = FirebaseRepository()

    private val _authError = MutableLiveData<String?>()
    val authError: LiveData<String?> = _authError

    init {
        firebaseRepository.getInstance().addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun signIn(email: String, password: String, loginActivity: LoginActivity) {
        firebaseRepository.signIn(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authError.value = null // Clear errors if sign-in is successful
                } else {
                    _authError.value = task.exception?.localizedMessage
                }
            }
    }

    fun signUp(email: String, password: String, signupActivity: SignupActivity) {
        firebaseRepository.signUp(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authError.value = null // Clear errors if signup is successful
                } else {
                    _authError.value = task.exception?.localizedMessage
                }
            }
    }

    fun signOut() {
        firebaseRepository.signOut()
    }
}
