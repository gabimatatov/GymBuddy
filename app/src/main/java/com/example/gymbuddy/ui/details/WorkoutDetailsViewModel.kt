package com.example.gymbuddy.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.models.Model
import com.example.gymbuddy.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth

class WorkoutDetailsViewModel : ViewModel() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val userRepository = UserRepository()

    private val _isOwner = MutableLiveData<Boolean>()
    val isOwner: LiveData<Boolean> get() = _isOwner

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> get() = _deleteSuccess

    private val _favoriteSuccess = MutableLiveData<Boolean>()
    val favoriteSuccess: LiveData<Boolean> get() = _favoriteSuccess

    fun checkIfUserIsOwner(workoutOwner: String) {
        val currentUserEmail = auth.currentUser?.email
        _isOwner.postValue(currentUserEmail == workoutOwner)
    }

    fun deleteWorkout(workoutId: String) {
        Model.shared.deleteWorkoutById(workoutId) { success ->
            _deleteSuccess.postValue(success)
        }
    }

    fun addToFavorites(workoutId: String) {
        val userId = auth.currentUser?.uid ?: return

        userRepository.fetchUser(userId, onSuccess = { user ->
            val updatedFavorites = user.favoriteWorkoutIds.toMutableList()

            if (!updatedFavorites.contains(workoutId)) {
                updatedFavorites.add(workoutId)
                userRepository.updateUserFavoriteWorkoutIds(userId, updatedFavorites, {
                    _favoriteSuccess.postValue(true)
                }, {
                    _favoriteSuccess.postValue(false)
                })
            } else {
                _favoriteSuccess.postValue(false)
            }
        }, onFailure = {
            _favoriteSuccess.postValue(false)
        })
    }
}

