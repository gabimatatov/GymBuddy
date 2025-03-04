package com.example.gymbuddy.ui.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.dataclass.User
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.repos.UserRepository
import com.example.gymbuddy.repos.WorkoutRepository

class ProfileViewModel(private val userId: String, private val email: String) : ViewModel() {

    private val userRepository = UserRepository()
    private val workoutRepository = WorkoutRepository()

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
        Log.d("ProfileViewModel", "Fetching workouts for user: $userId")
        workoutRepository.getUserWorkouts(userId,
            onSuccess = { workouts ->
                Log.d("ProfileViewModel", "Fetched ${workouts.size} workouts")
                _userWorkouts.postValue(workouts)
            },
            onFailure = { exception ->
                Log.e("ProfileViewModel", "Failed to fetch workouts: ${exception.message}")
                _userWorkouts.postValue(emptyList())
            }
        )
    }

    fun updateUser(user: User) {
        userRepository.updateUser(user, onSuccess = { fetchUser() }, onFailure = {})
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
