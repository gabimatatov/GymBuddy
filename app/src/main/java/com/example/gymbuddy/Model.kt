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
import java.util.concurrent.Executors

class Model private constructor() {

    private val database: AppLocalDbRepository = AppLocalDb.database
    private var executor = Executors.newSingleThreadExecutor()
    private val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    val workouts: MutableList<Workout> = ArrayList()

    companion object {
        val shared = Model()
    }

    fun getAllWorkouts(callback: (List<Workout>) -> Unit) {
        executor.execute {
            val workouts = database.workoutDao().getAllWorkouts()
            mainHandler.post {
                callback(workouts)
            }
        }
    }

    fun getWorkoutById(id: String): Workout {
        return database.workoutDao().getWorkoutById(id)
    }

    fun insertWorkouts(vararg workouts: Workout) {
        executor.execute {
            try {
                database.workoutDao().insertWorkouts(*workouts)
                println("Workout inserted successfully: ${workouts.size}")
            } catch (e: Exception) {
                println("Error inserting workout: ${e.message}")
            }
        }
    }

    fun deleteWorkout(workout: Workout) {
        executor.execute {
            database.workoutDao().delete(workout)
        }
    }

}