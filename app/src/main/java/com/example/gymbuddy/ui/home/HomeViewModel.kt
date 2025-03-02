package com.example.gymbuddy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymbuddy.Model
import com.example.gymbuddy.dataclass.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _workouts = MutableLiveData<List<Workout>>()
    val workouts: LiveData<List<Workout>> get() = _workouts

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {
        fetchWorkouts(null) // Load all workouts initially
    }

    fun fetchWorkouts(difficulty: String?) {
        Model.shared.getAllWorkouts { workoutList ->
            val filteredList = if (difficulty == null || difficulty == "All Difficulties") {
                workoutList
            } else {
                workoutList.filter { it.difficulty == difficulty }
            }
            _workouts.postValue(filteredList)
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch(Dispatchers.IO) {
            Model.shared.deleteWorkout(workout)
            fetchWorkouts(null) // Refresh the workout list after deletion
        }
    }
}
