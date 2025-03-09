package com.example.gymbuddy.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.db.AppLocalDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.gymbuddy.repos.UserRepository

class FavoritesViewModel : ViewModel() {

    private val _favorites = MutableLiveData<List<Workout>>()
    val favorites: LiveData<List<Workout>> get() = _favorites

    private val userRepository = UserRepository()

    fun loadFavorites(userId: String) {
        viewModelScope.launch {
            userRepository.getUserFavoriteWorkoutIds(userId, onSuccess = { favoriteWorkoutIds ->
                viewModelScope.launch {
                    val favoriteWorkouts = withContext(Dispatchers.IO) {
                        // Pass a list of IDs to the DAO method
                        AppLocalDb.database.workoutDao().getWorkoutsByIds(favoriteWorkoutIds)
                    }
                    _favorites.postValue(favoriteWorkouts)
                }
            }, onFailure = {
                _favorites.postValue(emptyList()) // Handle failure
            })
        }
    }
}

