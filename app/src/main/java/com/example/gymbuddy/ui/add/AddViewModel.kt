package com.example.gymbuddy.ui.add

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymbuddy.models.Model
import com.example.gymbuddy.dataclass.Workout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.gymbuddy.repos.WorkoutRepository

class AddViewModel : ViewModel() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val workoutRepository = WorkoutRepository()

    private val _workoutSaved = MutableLiveData<Boolean>()
    val workoutSaved: LiveData<Boolean> get() = _workoutSaved

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun saveWorkout(
        name: String,
        description: String,
        exercises: String,
        difficulty: String,
        imageBitmap: Bitmap?,
    ) {
        if (name.isEmpty() || description.isEmpty() || exercises.isEmpty()) {
            _errorMessage.value = "Please fill all fields"
            return
        }

        val userEmail = auth.currentUser?.email ?: "unknown_user"
        val currentTime = System.currentTimeMillis()

        val workout = Workout(
            workoutId = UUID.randomUUID().toString(),
            name = name,
            description = description,
            imageUrl = "",
            exercises = exercises,
            ownerId = userEmail,
            difficulty = difficulty,
            timestamp = currentTime,
            lastUpdated = currentTime
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (imageBitmap != null) {
                    workoutRepository.uploadImage(imageBitmap,
                        onSuccess = { imageUrl ->
                            workout.imageUrl = imageUrl
                            Model.shared.insertWorkouts(workout)
                        },
                        onFailure = { error ->
                            _errorMessage.postValue("Image upload failed: ${error.message}")
                        })
                    _workoutSaved.postValue(true)
                } else {
                    Model.shared.insertWorkouts(workout)
                    _workoutSaved.postValue(true)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error saving workout: ${e.message}")
            }
        }
    }
}
