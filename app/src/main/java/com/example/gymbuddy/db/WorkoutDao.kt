package com.example.gymbuddy.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymbuddy.dataclass.Workout

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM Workout ORDER BY timestamp DESC")
    fun getAllWorkouts(): List<Workout>

    @Query("SELECT * FROM Workout WHERE workoutId =:id")
    fun getWorkoutById(id: String): Workout

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkouts(vararg workouts: Workout)

    @Delete
    fun delete(workout: Workout)

    @Delete
    fun deleteWorkouts(vararg workouts: Workout)

    @Query("DELETE FROM Workout WHERE workoutId = :workoutId")
    fun deleteWorkoutById(workoutId: String)

}
