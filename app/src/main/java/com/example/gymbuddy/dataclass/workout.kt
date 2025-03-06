package com.example.gymbuddy.dataclass

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.example.gymbuddy.base.MyApplication

@Entity
data class Workout(
    @PrimaryKey val workoutId: String = "",
    val name: String = "",
    val description: String = "",
    var imageUrl: String = "",
    val exercises: String = "",
    val ownerId: String = "",
    val difficulty: String = "",
    val timestamp: Long = 0L,
    val lastUpdated: Long? = null
) {
    // Required no-argument constructor for Firestore
    constructor() : this("", "", "", "", "", "", "", 0L, null)

    companion object {
        // SharedPreferences for local last update tracking
        var lastUpdated: Long
            get() = MyApplication.Globals.context?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                ?.getLong(LOCAL_LAST_UPDATED, 0) ?: 0
            set(value) {
                MyApplication.Globals.context
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.apply {
                        edit().putLong(LOCAL_LAST_UPDATED, value).apply()
                    }
            }

        // Firestore keys
        const val ID_KEY = "workoutId"
        const val NAME_KEY = "name"
        const val DESCRIPTION_KEY = "description"
        const val IMAGE_URL_KEY = "imageUrl"
        const val EXERCISES_KEY = "exercises"
        const val OWNER_ID_KEY = "ownerId"
        const val DIFFICULTY_KEY = "difficulty"
        const val TIMESTAMP_KEY = "timestamp"
        const val LAST_UPDATED_KEY = "lastUpdated"
        const val LOCAL_LAST_UPDATED = "localWorkoutLastUpdated"

        // Convert Firestore JSON → Workout Object
        fun fromJSON(json: Map<String, Any>): Workout {
            val id = json[ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val imageUrl = json[IMAGE_URL_KEY] as? String ?: ""
            val exercises = json[EXERCISES_KEY] as? String ?: ""
            val ownerId = json[OWNER_ID_KEY] as? String ?: ""
            val difficulty = json[DIFFICULTY_KEY] as? String ?: ""
            val timestamp = (json[TIMESTAMP_KEY] as? Long) ?: System.currentTimeMillis()

            // Convert Firestore `Timestamp` to `Long`
            val firestoreTimestamp = json[LAST_UPDATED_KEY] as? Timestamp
            val lastUpdatedLong = firestoreTimestamp?.toDate()?.time

            return Workout(
                workoutId = id,
                name = name,
                description = description,
                imageUrl = imageUrl,
                exercises = exercises,
                ownerId = ownerId,
                difficulty = difficulty,
                timestamp = timestamp,
                lastUpdated = lastUpdatedLong
            )
        }
    }

    // Convert Workout Object → JSON for Firestore
    val json: Map<String, Any>
        get() = hashMapOf(
            ID_KEY to workoutId,
            NAME_KEY to name,
            DESCRIPTION_KEY to description,
            IMAGE_URL_KEY to imageUrl,
            EXERCISES_KEY to exercises,
            OWNER_ID_KEY to ownerId,
            DIFFICULTY_KEY to difficulty,
            TIMESTAMP_KEY to timestamp,
            LAST_UPDATED_KEY to FieldValue.serverTimestamp()
        )
}
