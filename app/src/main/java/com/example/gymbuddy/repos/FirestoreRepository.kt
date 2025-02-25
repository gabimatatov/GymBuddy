package com.example.gymbuddy.repos

import androidx.lifecycle.LiveData
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.dataclass.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Methods for User data
    fun getUserData(userId: String): LiveData<User> {
        val userDocRef = firestore.collection("users").document(userId)
        return FirestoreDocumentLiveData(userDocRef, User::class.java)
    }

    fun updateUser(user: User) {
        val userDocRef = firestore.collection("users").document(user.userId)
        userDocRef.set(user)
    }

    // Methods for Workout data (Renamed from Recipe)
    fun getWorkoutData(workoutId: String): LiveData<Workout> {
        val workoutDocRef = firestore.collection("workouts").document(workoutId)
        return FirestoreDocumentLiveData(workoutDocRef, Workout::class.java)
    }

    fun updateWorkout(workout: Workout) {
        val workoutDocRef = firestore.collection("workouts").document(workout.workoutId)
        workoutDocRef.set(workout)
    }

    // Retrieve all workouts
    fun getAllWorkouts(): LiveData<List<Workout>> {
        val workoutsCollectionRef = firestore.collection("workouts")
        return FirestoreCollectionLiveData(workoutsCollectionRef, Workout::class.java)
    }

    fun getWorkoutsByDifficulty(difficulty: String): LiveData<List<Workout>> {
        val workoutsCollectionRef = firestore.collection("workouts")
            .whereEqualTo("difficulty", difficulty)
        return FirestoreCollectionLiveData(workoutsCollectionRef, Workout::class.java)
    }

    // Update user's workout list and favorites
    fun addUserWorkout(userId: String, workoutId: String) {
        val userDocRef = firestore.collection("users").document(userId)
        userDocRef.update("workoutIds", FieldValue.arrayUnion(workoutId))
    }

    fun addUserFavoriteWorkout(userId: String, workoutId: String) {
        val userDocRef = firestore.collection("users").document(userId)
        userDocRef.update("favoriteWorkoutIds", FieldValue.arrayUnion(workoutId))
    }
}
