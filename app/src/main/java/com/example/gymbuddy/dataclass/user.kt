package com.example.gymbuddy.dataclass

data class User(
    val userId: String,
    val name: String,
    val photoUrl: String,
    val recipeIds: List<String>,
    val favoriteWorkoutIds: List<String>,
    val ratedWorkouts: Map<String, Int>
)