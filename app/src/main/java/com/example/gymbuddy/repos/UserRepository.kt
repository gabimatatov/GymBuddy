package com.example.gymbuddy.repos

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.gymbuddy.dataclass.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.example.gymbuddy.base.MyApplication.Globals.context
import java.util.*

class UserRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // Function to initialize user document
    fun initializeUserDocument(
        userId: String,
        email: String?
    ) {
        val initialUserData = hashMapOf(
            "userId" to userId,
            "email" to email,
            "name" to "",
            "photoUrl" to "",
            "workoutIds" to emptyList<String>(),
            "favoriteWorkoutIds" to emptyList<String>(),
            "ratedWorkouts" to emptyMap<String, Int>()
        )

        db.collection("users")
            .document(userId)
            .set(initialUserData, SetOptions.mergeFields("userId", "email"))
            .addOnSuccessListener {
                // Document initialization successful
                Log.d("initializeUser", "success")
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("initializeUser", "failed: ${exception.message}")
            }
    }

    // Function to fetch user data from Firestore
    fun fetchUser(
        userId: String,
        onSuccess: (User) -> Unit,
        onFailure: () -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    user?.let { onSuccess(it) }
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("fetchUser", "failed: ${exception.message}")
                onFailure()
            }
    }

    // Function to update user name in Firestore
    fun updateUserName(
        userId: String,
        newName: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .update("name", newName)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("updateUserName", "failed: ${exception.message}")
                onFailure()
            }
    }

    // Function to update user favorite workout IDs in Firestore
    fun updateUserFavoriteWorkoutIds(
        userId: String,
        newFavoriteWorkoutIds: List<String>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .update("favoriteWorkoutIds", newFavoriteWorkoutIds)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("updateUserFavoriteWorkoutIds", "failed: ${exception.message}")
                onFailure()
            }
    }

    // Function to update user photo URL in Firestore
    fun updateUserPhoto(
        userId: String,
        bitmap: Bitmap,
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        // Save the Bitmap as a file
        val file = saveBitmapToFile(bitmap)

        // Upload the file to Firebase Storage
        uploadImage(file, onSuccess = { imageUrl ->
            // Update the user document with the new photoUrl
            db.collection("users")
                .document(userId)
                .update("photoUrl", imageUrl)
                .addOnSuccessListener {
                    // Photo URL updated successfully
                    onSuccess(imageUrl)
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Log.d("updateUserPhoto", "failed to update photoUrl: ${exception.message}")
                    onFailure()
                }
        }, onFailure = onFailure)
    }

    // Function to save Bitmap to a file in internal storage
    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val file = File(context?.cacheDir, "${UUID.randomUUID()}.jpg")
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        } catch (e: IOException) {
            Log.e("UserRepository", "Error saving bitmap to file: ${e.message}")
        }
        return file
    }

    // Function to upload the file to Firebase Storage
    private fun uploadImage(
        file: File,
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        val storageRef: StorageReference = storage.reference
        val imageRef: StorageReference = storageRef.child("userImages/${file.name}")

        imageRef.putFile(Uri.fromFile(file))
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    onSuccess(imageUrl)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("uploadImage", "failed: ${exception.message}")
                onFailure()
            }
    }

    fun deleteUserPhoto(
        userId: String,
        onSuccess: (User) -> Unit,
        onFailure: () -> Unit
    ) {
        val userDocRef = db.collection("users").document(userId)

        // Get the current photo URL
        userDocRef.get().addOnSuccessListener { document ->
            val photoUrl = document.getString("photoUrl")

            if (!photoUrl.isNullOrEmpty()) {
                // Delete the image from Firebase Storage
                val storageRef = storage.getReferenceFromUrl(photoUrl)
                storageRef.delete().addOnSuccessListener {
                    // Deleted from Storage, update the Firestore document to remove the photoUrl
                    userDocRef.update("photoUrl", "")
                        .addOnSuccessListener {
                            // Success, return updated user data
                            val updatedUser = User(userId, document.getString("displayName") ?: "")
                            onSuccess(updatedUser)
                        }
                        .addOnFailureListener {
                            onFailure()
                        }
                }.addOnFailureListener {
                    onFailure()
                }
            } else {
                onSuccess(User(userId))
            }
        }.addOnFailureListener {
            onFailure()
        }
    }

    fun getUserFavoriteWorkoutIds(
        userId: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: () -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoriteWorkoutIds = document.get("favoriteWorkoutIds") as? List<String> ?: emptyList()

                    // Fetch all valid workouts to cross-check
                    db.collection("workouts")
                        .get()
                        .addOnSuccessListener { workoutDocuments ->
                            val validWorkoutIds = workoutDocuments.map { it.id }
                            val validFavorites = favoriteWorkoutIds.filter { it in validWorkoutIds }

                            // If there are invalid workout IDs, update Firestore
                            if (favoriteWorkoutIds.size != validFavorites.size) {
                                updateUserFavoriteWorkoutIds(userId, validFavorites,
                                    onSuccess = {
                                        Log.d("updateUserFavoriteWorkoutIds", "Removed deleted workouts from favorites.")
                                    },
                                    onFailure = {
                                        Log.d("updateUserFavoriteWorkoutIds", "Failed to update favorites.")
                                    }
                                )
                            }
                            onSuccess(validFavorites)
                        }
                        .addOnFailureListener {
                            Log.d("getUserFavoriteWorkoutIds", "Failed to fetch workouts.")
                            onSuccess(favoriteWorkoutIds) // Return existing favorites even if check fails
                        }
                } else {
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                Log.d("getUserFavoriteWorkoutIds", "Failed: ${exception.message}")
                onFailure()
            }
    }
}