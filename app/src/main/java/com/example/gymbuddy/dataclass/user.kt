package com.example.gymbuddy.dataclass

data class User(
    val userId: String,
    var name: String,
    var photoUrl: String?,
    var recipeIds: MutableList<String>
)