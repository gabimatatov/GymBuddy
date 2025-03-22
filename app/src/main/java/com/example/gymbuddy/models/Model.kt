package com.example.gymbuddy.models

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.db.AppLocalDb
import com.example.gymbuddy.db.AppLocalDbRepository
import com.example.gymbuddy.repos.UserRepository
import com.example.gymbuddy.repos.WorkoutRepository
import java.util.concurrent.Executors

class Model private constructor() {

    private val database: AppLocalDbRepository = AppLocalDb.database
    private var executor = Executors.newSingleThreadExecutor()
    private val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    private val workoutRepository = WorkoutRepository()
    private val userRepository = UserRepository()

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

                val workoutsToDelete = localWorkouts.filter { it.workoutId !in firestoreWorkoutIds }

                if (workoutsToDelete.isNotEmpty()) {
                    println("Deleting ${workoutsToDelete.size} missing workouts from local DB.")
                    database.workoutDao().deleteWorkouts(*workoutsToDelete.toTypedArray())
                }

                val latestUpdatedTime = workoutsFromFirestore.maxOfOrNull { it.lastUpdated ?: lastUpdated } ?: lastUpdated
                Workout.lastUpdated = latestUpdatedTime
                database.workoutDao().insertWorkouts(*workoutsFromFirestore.toTypedArray())

                val updatedWorkouts = database.workoutDao().getAllWorkouts()
                println("Local DB now contains ${updatedWorkouts.size} workouts.")
                mainHandler.post { callback(updatedWorkouts) }
            }
        }
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
        workoutId: String,
        name: String,
        description: String,
        exercises: String,
        difficulty: String,
        imageUrl: String,
        callback: (Boolean) -> Unit
    ) {
        val updatedWorkout = hashMapOf(
            "name" to name,
            "description" to description,
            "exercises" to exercises,
            "difficulty" to difficulty,
            "imageUrl" to imageUrl,
            "lastUpdated" to System.currentTimeMillis()
        )

        workoutRepository.updateWorkout(workoutId, updatedWorkout,
            onSuccess = {
                executor.execute {
                    database.workoutDao().updateWorkout(
                        workoutId, name, description, exercises, difficulty, imageUrl, System.currentTimeMillis()
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

    fun getUserWorkouts(ownerId: String, callback: (List<Workout>) -> Unit) {
        workoutRepository.getUserWorkouts(ownerId, onSuccess = { workoutsFromFirestore ->
            executor.execute {
                val localWorkouts = database.workoutDao().getAllWorkouts()
                val firestoreWorkoutIds = workoutsFromFirestore.map { it.workoutId }.toSet()

                val workoutsToDelete = localWorkouts.filter { it.workoutId !in firestoreWorkoutIds }

                if (workoutsToDelete.isNotEmpty()) {
                    println("Deleting ${workoutsToDelete.size} missing workouts from local DB.")
                    database.workoutDao().deleteWorkouts(*workoutsToDelete.toTypedArray())
                }

                database.workoutDao().insertWorkouts(*workoutsFromFirestore.toTypedArray())

                val updatedWorkouts = database.workoutDao().getAllWorkouts()
                println("Local DB now contains ${updatedWorkouts.size} workouts.")
                mainHandler.post { callback(updatedWorkouts) }
            }
        }, onFailure = {
            println("Failed to fetch workouts from Firestore for user: $ownerId")
            mainHandler.post { callback(emptyList()) }
        })
    }

    fun getUserFavorites(userId: String, since: Long, callback: (List<Workout>) -> Unit) {
        userRepository.getUserFavoriteWorkoutIds(userId, { favoriteWorkoutIds ->
            workoutRepository.getAllWorkouts(since) { allWorkouts ->
                val combinedWorkouts = allWorkouts.filter { workout ->
                    workout.ownerId == userId || favoriteWorkoutIds.contains(workout.workoutId)
                }
                callback(combinedWorkouts)
            }
        }, {
            // Handle failure
            callback(emptyList())
        })
    }
}