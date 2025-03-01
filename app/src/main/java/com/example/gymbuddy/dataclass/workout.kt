package com.example.gymbuddy.dataclass

data class Workout(
    val workoutId: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val exercises: String = "",
    val ownerId: String = "",
    val difficulty: String = ""
    //val rating: Float,
    //val numberOfRatings: Int,
) {
    constructor() : this("", "", "", "", "", "", "")
}