package com.example.gymbuddy.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymbuddy.Model
import com.example.gymbuddy.dataclass.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class AddViewModel : ViewModel() {

    private val _workoutSaved = MutableLiveData<Boolean>()
    val workoutSaved: LiveData<Boolean> get() = _workoutSaved

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun saveWorkout(name: String, description: String, exercises: String, difficulty: String) {
        if (name.isEmpty() || description.isEmpty() || exercises.isEmpty()) {
            _errorMessage.value = "Please fill all fields"
            return
        }

        val workout = Workout(
            workoutId = UUID.randomUUID().toString(),
            name = name,
            description = description,
            imageUrl = "",  // Assuming no image URL for local DB
            exercises = exercises,
            ownerId = "local", // No Firebase, so use a placeholder
            difficulty = difficulty
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Model.shared.insertWorkouts(workout)
                _workoutSaved.postValue(true)
            } catch (e: Exception) {
                _errorMessage.postValue("Error saving workout: ${e.message}")
            }
        }
    }
}
