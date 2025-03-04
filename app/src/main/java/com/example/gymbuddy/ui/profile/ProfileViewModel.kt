package com.example.gymbuddy.ui.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.dataclass.User
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.models.Model
import com.example.gymbuddy.repos.UserRepository

class ProfileViewModel(private val userId: String, private val email: String) : ViewModel() {

    private val userRepository = UserRepository()

    private val _userLiveData: MutableLiveData<User> = MutableLiveData()
    val userLiveData: LiveData<User> get() = _userLiveData

    private val _userWorkouts: MutableLiveData<List<Workout>> = MutableLiveData()
    val userWorkouts: LiveData<List<Workout>> get() = _userWorkouts

    init {
        userRepository.initializeUserDocument(userId, email)
        fetchUser()
        fetchUserWorkouts() // Fetch workouts when ViewModel is created
    }

    private fun fetchUser() {
        userRepository.fetchUser(userId,
            onSuccess = { user ->
                _userLiveData.postValue(user)
                fetchUserWorkouts() // Fetch workouts again in case user data updates
            },
            onFailure = {
                // Handle failure
            }
        )
    }

    private fun fetchUserWorkouts() {
        Log.d("ProfileViewModel", "Fetching workouts for user: $email")

        Model.shared.getUserWorkouts(email) { workouts ->
            Log.d("ProfileViewModel", "Fetched ${workouts.size} workouts from local DB")
            _userWorkouts.postValue(workouts)
        }
    }

    fun updateUserName(newName: String) {
        userRepository.updateUserName(userId, newName, onSuccess = { fetchUser() }, onFailure = {})
    }

    fun updateUserPhoto(bitmap: Bitmap) {
        userRepository.updateUserPhoto(userId, bitmap, onSuccess = { fetchUser() }, onFailure = {})
    }

    fun deleteUserPhoto() {
        userRepository.deleteUserPhoto(userId, onSuccess = { fetchUser() }, onFailure = {})
    }
}
