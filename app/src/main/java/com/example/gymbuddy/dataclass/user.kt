package com.example.gymbuddy.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val favoriteWorkoutIds: List<String> = emptyList(),
) {
    // Add a no-argument constructor
    constructor() : this("", "", "","", emptyList())
}