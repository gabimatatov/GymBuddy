package com.example.gymbuddy.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.models.Model
import com.google.firebase.auth.FirebaseAuth

class WorkoutDetailsViewModel : ViewModel() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val _isOwner = MutableLiveData<Boolean>()
    val isOwner: LiveData<Boolean> get() = _isOwner

    fun checkIfUserIsOwner(workoutOwner: String) {
        val currentUserEmail = auth.currentUser?.email
        _isOwner.postValue(currentUserEmail == workoutOwner)
    }

    fun deleteWorkout(workoutId: String) {
        Model.shared.deleteWorkoutById(workoutId)
    }
}
