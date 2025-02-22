package com.example.gymbuddy.repos

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class FirebaseRepository(private val auth: FirebaseAuth) {

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
        user?.updateProfile(UserProfileChangeRequest.Builder().setPhotoUri(photoUri).build())
    }
}