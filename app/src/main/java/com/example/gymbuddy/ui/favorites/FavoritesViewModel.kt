package com.example.gymbuddy.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.models.Model

class FavoritesViewModel : ViewModel() {
    private val _favorites = MutableLiveData<List<Workout>>()
    val favorites: LiveData<List<Workout>> = _favorites

    private val model = Model.shared

    // Method to load user's favorite workouts
    fun loadFavorites(userId: String, since: Long) {
        model.getUserFavorites(userId, since) { favoriteWorkouts ->
            _favorites.value = favoriteWorkouts
        }
    }
}

