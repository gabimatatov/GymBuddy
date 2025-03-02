package com.example.gymbuddy.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity
data class Workout(
    @PrimaryKey val workoutId: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val exercises: String = "",
    val ownerId: String = "",
    val difficulty: String = "",
    //val rating: Float,
    //val numberOfRatings: Int,
    val timestamp: Long = System.currentTimeMillis() // Store timestamp as Long
) {
    constructor() : this("", "", "", "", "", "", "")
}