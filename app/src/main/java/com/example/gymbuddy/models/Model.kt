package com.example.gymbuddy.models

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.db.AppLocalDb
import com.example.gymbuddy.db.AppLocalDbRepository
import com.example.gymbuddy.repos.WorkoutRepository
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executors

class Model private constructor() {

    private val database: AppLocalDbRepository = AppLocalDb.database
    private var executor = Executors.newSingleThreadExecutor()
    private val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    private val workoutRepository = WorkoutRepository()

    val workouts: MutableList<Workout> = ArrayList()

    companion object {
        val shared = Model()
    }

    fun getAllWorkouts(callback: (List<Workout>) -> Unit) {
        val lastUpdated = Workout.lastUpdated

        workoutRepository.getAllWorkouts(lastUpdated) { workoutsFromFirestore ->

            executor.execute {
                val localWorkouts = database.workoutDao().getAllWorkouts()
                val firestoreWorkoutIds = workoutsFromFirestore.map { it.workoutId }.toSet()

                // Identify workouts that exist locally but are missing in Firestore
                val workoutsToDelete = localWorkouts.filter { it.workoutId !in firestoreWorkoutIds }

                // Remove these workouts from the local database
                if (workoutsToDelete.isNotEmpty()) {
                    println("Deleting ${workoutsToDelete.size} missing workouts from local DB.")
                    database.workoutDao().deleteWorkouts(*workoutsToDelete.toTypedArray())
                }

                // Insert or update Firestore workouts in local DB
                val latestUpdatedTime = workoutsFromFirestore.maxOfOrNull { it.lastUpdated ?: lastUpdated } ?: lastUpdated
                Workout.lastUpdated = latestUpdatedTime
                database.workoutDao().insertWorkouts(*workoutsFromFirestore.toTypedArray())

                val updatedWorkouts = database.workoutDao().getAllWorkouts()
                println("Local DB now contains ${updatedWorkouts.size} workouts.")
                mainHandler.post { callback(updatedWorkouts) }
            }
        }
    }

    fun getWorkoutById(id: String): Workout {
        return database.workoutDao().getWorkoutById(id)
    }

    fun insertWorkouts(vararg workouts: Workout) {
        val currentTime = System.currentTimeMillis()

        workouts.forEach { workout ->
            val updatedWorkout = workout.copy(lastUpdated = currentTime)

            workoutRepository.addWorkout(updatedWorkout,
                onSuccess = {
                    println("Workout added to Firestore: ${updatedWorkout.workoutId}")
                },
                onFailure = { exception ->
                    println("Error adding workout to Firestore: ${exception.message}")
                }
            )
        }
    }

    fun deleteWorkout(workout: Workout) {
        workoutRepository.deleteWorkout(workout,
            onSuccess = {
                executor.execute {
                    database.workoutDao().delete(workout)
                    println("Successfully deleted workout: ${workout.workoutId} from Firestore and local DB.")
                }
            },
            onFailure = { exception ->
                println("Error deleting workout from Firestore: ${exception.message}")
            }
        )
    }

    fun deleteWorkoutById(workoutId: String, callback: (Boolean) -> Unit) {
        executor.execute {
            try {
                database.workoutDao().deleteWorkoutById(workoutId)
                workoutRepository.deleteWorkout(workoutId, onSuccess = {
                    callback(true)
                }, onFailure = {
                    callback(false)
                })
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    fun updateWorkout(
        workoutId: String, name: String, description: String, exercises: String, difficulty: String, callback: (Boolean) -> Unit
    ) {
        val updatedWorkout = hashMapOf(
            "name" to name,
            "description" to description,
            "exercises" to exercises,
            "difficulty" to difficulty,
            "lastUpdated" to System.currentTimeMillis()
        )

        workoutRepository.updateWorkout(workoutId, updatedWorkout,
            onSuccess = {
                executor.execute {
                    database.workoutDao().updateWorkout(
                        workoutId, name, description, exercises, difficulty, System.currentTimeMillis()
                    )
                    println("Workout updated in Firestore and locally: $workoutId")
                    callback(true)
                }
            },
            onFailure = {
                println("Failed to update workout in Firestore: $workoutId")
                callback(false)
            }
        )
    }

}