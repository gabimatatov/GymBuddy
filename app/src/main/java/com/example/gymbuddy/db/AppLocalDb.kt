package com.example.gymbuddy.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gymbuddy.base.MyApplication
import com.example.gymbuddy.dataclass.Workout

@Database(entities = [Workout::class], version = 6)
abstract class AppLocalDbRepository: RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}

object AppLocalDb {

    val database: AppLocalDbRepository by lazy {
        val context = MyApplication.Globals.context
            ?: throw IllegalStateException("Application context is missing")

        val db = Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        println("Database created at: " + context.getDatabasePath("dbFileName.db").absolutePath)
        db
    }

}