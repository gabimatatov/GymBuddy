package com.example.gymbuddy.repos

import android.net.Uri
import android.util.Log
import com.example.gymbuddy.dataclass.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class UserRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // Function to initialize user document
    fun initializeUserDocument(userId: String) {
        val initialUserData = hashMapOf(
            "userId" to userId,
            "name" to "",
            "photoUrl" to "",
            "workoutIds" to emptyList<String>(),
            "favoriteWorkoutIds" to emptyList<String>(),
            "ratedWorkouts" to emptyMap<String, Int>()
        )

        db.collection("users")
            .document(userId)
            .set(initialUserData, SetOptions.mergeFields("userId"))
            .addOnSuccessListener {
                // Document initialization successful
                Log.d("initializeUser", "success")
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.d("initializeUser", "failed: " + exception.message)
            }
    }

    // Function to fetch user data from Firestore
    fun fetchUser(userId: String, onSuccess: (User) -> Unit, onFailure: () -> Unit) {
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
                onFailure()
            }
    }

    // Function to update user data in Firestore
    fun updateUser(user: User, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection("users")
            .document(user.userId)
            .set(user)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                onFailure()
            }
    }

    // Function to update user name in Firestore
    fun updateUserName(userId: String, newName: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection("users")
            .document(userId)
            .update("name", newName)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                onFailure()
            }
    }

    // Function to update user photo in Firestore
    fun updateUserPhoto(userId: String, imageUri: Uri, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        uploadImage(
            userId,
            imageUri,
            onSuccess = { newPhotoUrl ->
                db.collection("users")
                    .document(userId)
                    .update("photoUrl", newPhotoUrl)
                    .addOnSuccessListener {
                        onSuccess(newPhotoUrl)
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                        onFailure()
                    }
            },
            onFailure = {
                onFailure()
            }
        )
    }

    // Function to update user workout IDs in Firestore
    fun updateUserWorkoutIds(userId: String, newWorkoutIds: List<String>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection("users")
            .document(userId)
            .update("workoutIds", newWorkoutIds)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                onFailure()
            }
    }

    // Function to update user favorite workout IDs in Firestore
    fun updateUserFavoriteWorkoutIds(userId: String, newFavoriteWorkoutIds: List<String>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection("users")
            .document(userId)
            .update("favoriteWorkoutIds", newFavoriteWorkoutIds)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                onFailure()
            }
    }

    // Function to update user rated recipes in Firestore
    fun updateUserRatedWorkouts(userId: String, newRatedWorkouts: Map<String, Int>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection("users")
            .document(userId)
            .update("ratedWorkouts", newRatedWorkouts)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                onFailure()
            }
    }

    // Function to upload an image to Firestore Storage
    private fun uploadImage(userId: String, imageUri: Uri, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val storageRef: StorageReference = storage.reference.child("user_photos/$userId/${UUID.randomUUID()}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully, get download URL
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                onFailure()
            }
    }
}