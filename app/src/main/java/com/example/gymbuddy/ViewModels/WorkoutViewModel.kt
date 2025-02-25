package com.example.gymbuddy.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.repos.FirestoreRepository

class RecipeViewModel : ViewModel() {

    private val repository = FirestoreRepository()
    private val _recipeData = MutableLiveData<Workout>()
    val recipeData: LiveData<Workout> get() = _recipeData

    fun fetchRecipeData(recipeId: String) {
        repository.getWorkoutData(recipeId).observeForever {
            _recipeData.value = it
        }
    }

    fun updateRecipe(recipe: Workout) {
        repository.updateWorkout(recipe)
    }

    fun getAllRecipes(): LiveData<List<Workout>> {
        return repository.getAllWorkouts()
    }

    fun getRecipesByCategory(difficulty: String): LiveData<List<Workout>> {
        return repository.getWorkoutsByDifficulty(difficulty)
    }

    // ... other methods
}