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

                for (workout in workoutsFromFirestore) {
                    database.workoutDao().insertWorkouts(workout)
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
                    executor.execute {
                        try {
                            database.workoutDao().insertWorkouts(updatedWorkout)
                            Workout.lastUpdated = currentTime
                            println("Workout inserted successfully: ${workouts.size}")
                        } catch (e: Exception) {
                            println("Error inserting workout: ${e.message}")
                        }
                    }
                },
                onFailure = { exception ->
                    println("Error adding workout to Firestore: ${exception.message}")
                }
            )
        }
    }


    fun deleteWorkout(workout: Workout) {
        executor.execute {
            database.workoutDao().delete(workout)
        }
    }

}