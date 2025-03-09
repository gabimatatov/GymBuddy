package com.example.gymbuddy.ui.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.repos.UserRepository
import com.example.gymbuddy.repos.WorkoutRepository

class FavoritesViewModel : ViewModel() {
    private val _favorites = MutableLiveData<List<Workout>>()
    val favorites: LiveData<List<Workout>> = _favorites

    private val userRepository = UserRepository()
    private val workoutRepository = WorkoutRepository()

    // Mode to Model
    // Method to load user's favorite workouts
    fun loadFavorites(userId: String, since: Long) {
        userRepository.getUserFavoriteWorkoutIds(userId, { favoriteWorkoutIds ->
            workoutRepository.getAllWorkouts(since) { allWorkouts ->
                val combinedWorkouts = allWorkouts.filter { workout ->
                    workout.ownerId == userId || favoriteWorkoutIds.contains(workout.workoutId)
                }
                _favorites.value = combinedWorkouts
            }
        }, {
            // Handle failure if needed
            Log.d("FavoritesViewModel", "Failed to fetch favorite workout IDs.")
        })
    }
}
