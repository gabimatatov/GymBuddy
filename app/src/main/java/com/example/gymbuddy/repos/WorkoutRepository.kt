package com.example.gymbuddy.repos

import android.util.Log
import com.example.gymbuddy.dataclass.Workout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import java.util.Date
import java.util.UUID

class WorkoutRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Function to add a new workout to Firestore
    fun addWorkout(workout: Workout, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val workoutId = UUID.randomUUID().toString() // Generate a unique ID
        val workoutWithId = workout.copy(workoutId = workoutId) // Assign the ID

        db.collection("workouts")
            .document(workoutId)
            .set(workoutWithId, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("WorkoutRepository", "Workout added successfully")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("WorkoutRepository", "Failed to add workout: ${exception.message}")
                onFailure(exception)
            }
    }

    fun getAllWorkouts(since: Long, callback: (List<Workout>) -> Unit) {
        db.collection("workouts")
            .whereGreaterThanOrEqualTo(Workout.LAST_UPDATED_KEY, Timestamp(Date(since)))
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val workouts: MutableList<Workout> = mutableListOf()
                    for (document in task.result) {
                        workouts.add(Workout.fromJSON(document.data))
                    }
                    callback(workouts)
                } else {
                    callback(listOf())
                }
            }
    }


    // Function to fetch all workout of specific difficulty
    fun getWorkoutsByDifficulty(difficulty: String?, onSuccess: (List<Workout>) -> Unit, onFailure: (Exception) -> Unit) {
        val query = if (difficulty == null) {
            db.collection("workouts").orderBy("timestamp", Query.Direction.DESCENDING) // Get all workouts
        } else {
            db.collection("workouts")
                .whereEqualTo("difficulty", difficulty)
                .orderBy("timestamp", Query.Direction.DESCENDING)
        }

        query.get()
            .addOnSuccessListener { result ->
                val workouts = result.mapNotNull { it.toObject(Workout::class.java) }
                onSuccess(workouts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
