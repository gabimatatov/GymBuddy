package com.example.gymbuddy.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.dataclass.User
import com.example.gymbuddy.repos.FirestoreRepository

class UserViewModel : ViewModel() {

    private val repository = FirestoreRepository()
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    fun fetchUserData(userId: String) {
        repository.getUserData(userId).observeForever {
            _userData.value = it
        }
    }

    fun updateUser(user: User) {
        repository.updateUser(user)
    }

    fun addUserRecipe(userId: String, workoutId: String) {
        repository.addUserWorkout(userId, workoutId)
    }

    fun addUserFavoriteRecipe(userId: String, workoutId: String) {
        repository.addUserFavoriteWorkout(userId, workoutId)
    }

    // ... other methods
}