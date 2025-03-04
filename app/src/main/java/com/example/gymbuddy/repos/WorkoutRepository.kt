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

    fun addWorkout(workout: Workout, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val workoutId = UUID.randomUUID().toString()
        val workoutWithId = workout.copy(workoutId = workoutId)

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
        println("Fetching workouts updated since: $since")

        db.collection("workouts")
            .whereGreaterThanOrEqualTo("lastUpdated", since)
            .orderBy("lastUpdated", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val workouts = task.result.mapNotNull { document ->
                        println("Found workout in Firestore: ${document.id}")
                        Workout.fromJSON(document.data)
                    }
                    callback(workouts)
                } else {
                    println("Firestore fetch failed: ${task.exception?.message}")
                    callback(listOf())
                }
            }
    }

    fun getUserWorkouts(ownerId: String, onSuccess: (List<Workout>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("workouts")
            .whereEqualTo("ownerId", ownerId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val workouts = result.mapNotNull { it.toObject(Workout::class.java) }
                Log.d("WorkoutRepository", "Fetched ${workouts.size} workouts for user: $ownerId")
                onSuccess(workouts)
            }
            .addOnFailureListener { exception ->
                Log.e("WorkoutRepository", "Failed to fetch workouts: ${exception.message}")
                onFailure(exception)
            }
    }

    fun deleteWorkout(workoutId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("workouts").document(workoutId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateWorkout(workoutId: String, updatedWorkout: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("workouts").document(workoutId)
            .update(updatedWorkout)
            .addOnSuccessListener {
                println("Workout updated in Firestore: $workoutId")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                println("Failed to update workout in Firestore: ${exception.message}")
                onFailure(exception)
            }
    }


}
