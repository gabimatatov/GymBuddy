package com.example.gymbuddy.repos

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.gymbuddy.base.MyApplication.Globals.context
import com.example.gymbuddy.dataclass.User
import com.example.gymbuddy.dataclass.Workout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class WorkoutRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

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

    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val file = File(context?.cacheDir, "${UUID.randomUUID()}.jpg")
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        } catch (e: IOException) {
            Log.e("WorkoutRepository", "Error saving bitmap to file: ${e.message}")
        }
        return file
    }

    fun uploadImage(bitmap: Bitmap, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val file = saveBitmapToFile(bitmap)
        val storageRef: StorageReference = storage.reference
        val imageRef: StorageReference = storageRef.child("workoutImages/${file.name}")

        imageRef.putFile(Uri.fromFile(file))
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.e("WorkoutRepository", "Image upload failed: ${exception.message}")
                onFailure(exception)
            }
    }

    fun deleteUserPhoto(workoutId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val workoutDocRef = db.collection("workouts").document(workoutId)

        // Get the current photo URL
        workoutDocRef.get().addOnSuccessListener { document ->
            val imageUrl = document.getString("imageUrl")

            if (!imageUrl.isNullOrEmpty()) {
                // Delete the image from Firebase Storage
                val storageRef = storage.getReferenceFromUrl(imageUrl)
                storageRef.delete().addOnSuccessListener {
                    // Deleted from Storage, update the Firestore document to remove the photoUrl
                    workoutDocRef.update("imageUrl", "")
                        .addOnSuccessListener {
                            // Success, return updated user data
//                            val updatedWorkout = Workout(workoutId, document.getString("displayName") ?: "")
//                            onSuccess(updatedWorkout)
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onFailure()
                        }
                }.addOnFailureListener {
                    onFailure()
                }
            } else {
                onSuccess()
            }
        }.addOnFailureListener {
            onFailure()
        }
    }
}
