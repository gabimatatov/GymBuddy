package com.example.gymbuddy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.repos.WorkoutRepository

class HomeViewModel : ViewModel() {

    private val workoutRepository = WorkoutRepository()

    private val _workouts = MutableLiveData<List<Workout>>()
    val workouts: LiveData<List<Workout>> get() = _workouts

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {
        fetchWorkouts("All")
    }

    fun fetchWorkouts(difficulty: String?) {
        workoutRepository.getWorkoutsByDifficulty(difficulty,
            onSuccess = { workoutList -> _workouts.value = workoutList },
            onFailure = { exception -> _errorMessage.value = "Error fetching workouts: ${exception.message}" }
        )
    }
}
