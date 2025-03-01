package com.example.gymbuddy.dataclass

import com.google.firebase.Timestamp

data class Workout(
    val workoutId: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val exercises: String = "",
    val ownerId: String = "",
    val difficulty: String = "",
    //val rating: Float,
    //val numberOfRatings: Int,
    val timestamp: Timestamp = Timestamp.now()
) {
    constructor() : this("", "", "", "", "", "", "")
}