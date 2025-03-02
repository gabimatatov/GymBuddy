package com.example.gymbuddy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymbuddy.Model
import com.example.gymbuddy.dataclass.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _workouts = MutableLiveData<List<Workout>>()
    val workouts: LiveData<List<Workout>> get() = _workouts

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    init {
        fetchWorkouts(null)
    }

    fun fetchWorkouts(difficulty: String?) {
        _loadingState.postValue(true) // Show loading indicator

        viewModelScope.launch(Dispatchers.IO) {
            try {
                delay(500) // Simulate 0.5 seconds delay for testing
                Model.shared.getAllWorkouts { workoutList ->
                    val filteredList = if (difficulty == null || difficulty == "All Difficulties") {
                        workoutList
                    } else {
                        workoutList.filter { it.difficulty == difficulty }
                    }
                    _workouts.postValue(filteredList)
                }
            } finally {
                _loadingState.postValue(false) // Hide loading indicator
            }
        }
    }



    fun deleteWorkout(workout: Workout) {
        _loadingState.postValue(true) // Show loading while deleting

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Model.shared.deleteWorkout(workout)
                fetchWorkouts(null) // Refresh after deletion
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            } finally {
                _loadingState.postValue(false) // Hide loading
            }
        }
    }
}
