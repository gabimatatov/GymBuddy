package com.example.gymbuddy.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.repos.WorkoutRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class AddViewModel : ViewModel() {

    private val workoutRepository = WorkoutRepository()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _workoutSaved = MutableLiveData<Boolean>()
    val workoutSaved: LiveData<Boolean> get() = _workoutSaved

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun saveWorkout(name: String, description: String, exercises: String, difficulty: String) {
        if (name.isEmpty() || description.isEmpty() || exercises.isEmpty()) {
            _errorMessage.value = "Please fill all fields"
            return
        }

        auth.currentUser?.email?.let { email ->
            val workout = Workout(
                workoutId = UUID.randomUUID().toString(),
                name = name,
                description = description,
                imageUrl = "",
                exercises = exercises,
                ownerId = email,
                difficulty = difficulty
            )

            workoutRepository.addWorkout(workout,
                onSuccess = { _workoutSaved.value = true },
                onFailure = { exception -> _errorMessage.value = exception.message }
            )
        } ?: run {
            _errorMessage.value = "User not signed in!"
        }
    }
}
