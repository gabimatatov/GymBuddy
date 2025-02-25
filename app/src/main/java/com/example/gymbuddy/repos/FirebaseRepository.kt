package com.example.gymbuddy.repos

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class FirebaseRepository(private val auth: FirebaseAuth) {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()


    fun signIn(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun signUp(email: String, password: String, name: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun updateDisplayName(name: String) {
        val user = auth.currentUser
        user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
    }

    fun updateUserPhoto(photoUri: Uri) {
        val user = auth.currentUser
        user?.let { currentUser ->
            uploadImageToFirebaseStorage(currentUser.uid, photoUri)
        }
    }

    private fun uploadImageToFirebaseStorage(userId: String, imageUri: Uri) {
        val storageRef: StorageReference = storage.reference
        val imagesRef: StorageReference = storageRef.child("profile_images/$userId/${UUID.randomUUID()}")

        imagesRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                // Image uploaded successfully, now get the download URL
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    updateUserPhotoUri(uri)
                }
            }
            .addOnFailureListener {
                // Handle failed image upload
            }
    }

    private fun updateUserPhotoUri(photoUri: Uri) {
        val user = auth.currentUser
        user?.updateProfile(UserProfileChangeRequest.Builder().setPhotoUri(photoUri).build())
    }

}