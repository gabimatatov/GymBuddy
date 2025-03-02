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

//    init {
//        fetchWorkouts(null)
//    }

    fun fetchWorkouts(difficulty: String?) {
        println("🔄 FetchWorkouts called with difficulty: $difficulty")

        _loadingState.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            Model.shared.getAllWorkouts { workoutList ->
                val filteredList = if (difficulty == null || difficulty == "All Difficulties") {
                    workoutList.distinctBy { it.workoutId } // Remove duplicates before updating LiveData
                } else {
                    workoutList.filter { it.difficulty == difficulty }.distinctBy { it.workoutId }
                }
                _workouts.postValue(filteredList)
                _loadingState.postValue(false)
            }
        }
    }


    fun deleteWorkout(workout: Workout) {
        _loadingState.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            Model.shared.deleteWorkout(workout)

            // Refresh UI after deletion
            fetchWorkouts(null)

            _loadingState.postValue(false)
        }
    }


}
