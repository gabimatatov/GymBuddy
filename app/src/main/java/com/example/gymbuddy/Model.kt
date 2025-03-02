package com.example.gymbuddy

import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.db.AppLocalDb
import com.example.gymbuddy.db.AppLocalDbRepository
import com.example.gymbuddy.repos.WorkoutRepository
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
                var latestUpdatedTime = lastUpdated
                val existingWorkouts = database.workoutDao().getAllWorkouts().map { it.workoutId }.toSet() // Get current IDs

                for (workout in workoutsFromFirestore) {
                    if (!existingWorkouts.contains(workout.workoutId)) { // Prevent duplicate insert
                        database.workoutDao().insertWorkouts(workout)
                    }

                    workout.lastUpdated?.let { workoutLastUpdated ->
                        if (latestUpdatedTime < workoutLastUpdated) {
                            latestUpdatedTime = workoutLastUpdated
                        }
                    }
                }

                Workout.lastUpdated = latestUpdatedTime
                val updatedWorkouts = database.workoutDao().getAllWorkouts()
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

}