package com.example.gymbuddy.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.dataclass.User
import com.example.gymbuddy.repos.UserRepository

class UserViewModel(private val userId: String) : ViewModel() {

    private val userRepository = UserRepository()
    private val _userLiveData: MutableLiveData<User> = MutableLiveData()
    val userLiveData: LiveData<User> get() = _userLiveData

    init {
        // Initialize user document on initialization
        userRepository.initializeUserDocument(userId)
        // Fetch user data on initialization
        fetchUser()
    }

    private fun fetchUser() {
        userRepository.fetchUser(userId,
            onSuccess = { user ->
                _userLiveData.postValue(user)
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUser(user: User) {
        userRepository.updateUser(user,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUserName(newName: String) {
        userRepository.updateUserName(userId, newName,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUserPhoto(imageUri: Uri) {
        userRepository.updateUserPhoto(userId, imageUri,
            onSuccess = { newPhotoUrl ->
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUserWorkoutIds(newWorkoutIds: List<String>) {
        userRepository.updateUserWorkoutIds(userId, newWorkoutIds,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUserFavoriteWorkoutIds(newFavoriteWorkoutIds: List<String>) {
        userRepository.updateUserFavoriteWorkoutIds(userId, newFavoriteWorkoutIds,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    fun updateUserRatedWorkouts(newRatedWorkouts: Map<String, Int>) {
        userRepository.updateUserRatedWorkouts(userId, newRatedWorkouts,
            onSuccess = {
                // After a successful update, fetch the user again to reflect changes
                fetchUser()
            },
            onFailure = {
                // Handle failure
            }
        )
    }
}