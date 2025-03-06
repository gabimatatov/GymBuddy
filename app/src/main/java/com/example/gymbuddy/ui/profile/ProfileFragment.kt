package com.example.gymbuddy.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.example.gymbuddy.R
import com.example.gymbuddy.activities.AuthViewModel
import com.example.gymbuddy.dataclass.User
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.objects.CameraUtil
import com.example.gymbuddy.ui.dialog.EditDisplayNameDialogFragment
import com.squareup.picasso.Picasso
import com.example.gymbuddy.adapters.WorkoutAdapter

class ProfileFragment : Fragment(), EditDisplayNameDialogFragment.EditUsernameDialogListener,
    CameraUtil.CameraResultCallback {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userPhotoImageView: ImageView
    private lateinit var cameraUtil: CameraUtil
    private lateinit var myWorkoutsRecyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        cameraUtil = CameraUtil(this)
        cameraUtil.setCameraResultCallback(this)

        val displayNameTextView: TextView = view.findViewById(R.id.displayNameTextView)
        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        val deleteImageButton: Button = view.findViewById(R.id.deleteImageButton)
        userPhotoImageView = view.findViewById(R.id.userPhotoImageView)
        myWorkoutsRecyclerView = view.findViewById(R.id.myWorkoutsRecyclerView)

        deleteImageButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        displayNameTextView.setOnClickListener {
            val dialogFragment = EditDisplayNameDialogFragment()
            dialogFragment.show(childFragmentManager, "EditDisplayNameDialogFragment")
        }

        userPhotoImageView.setOnClickListener {
            cameraUtil.checkCameraPermission()
        }

        // Setup RecyclerView for user workouts
        workoutAdapter = WorkoutAdapter(emptyList()) { selectedWorkout ->
            navigateToWorkoutDetails(selectedWorkout)
        }

        myWorkoutsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = workoutAdapter
        }

        authViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                emailTextView.text = user.email
                displayNameTextView.text = user.displayName
                profileViewModel = ProfileViewModel(user.uid ?: "", user.email ?: "")

                profileViewModel.userLiveData.observe(viewLifecycleOwner, Observer { userData ->
                    userData?.let { updateUI(userData) }
                })

                profileViewModel.userWorkouts.observe(viewLifecycleOwner, Observer { workouts ->
                    Log.d("ProfileFragment", "Received ${workouts.size} workouts")
                    workoutAdapter.updateData(workouts)
                })
            }
        })
    }

    private fun navigateToWorkoutDetails(workout: Workout) {
        val action = ProfileFragmentDirections.actionNavigationProfileToWorkoutDetailsFragment(
            workoutId = workout.workoutId,
            workoutName = workout.name,
            workoutDescription = workout.description,
            workoutExercises = workout.exercises,
            workoutDifficulty = workout.difficulty,
            workoutOwner = workout.ownerId,
            workoutImageUrl = workout.imageUrl
        )
        findNavController().navigate(action)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        cameraUtil.processActivityResult(requestCode, resultCode, data)
    }

    override fun onImageCaptured(bitmap: Bitmap) {
        uploadImageToFirebase(bitmap)
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {
        profileViewModel.updateUserPhoto(bitmap)
        Toast.makeText(requireContext(), "Photo Updated", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(userData: User) {
        val displayNameTextView: TextView? = view?.findViewById(R.id.displayNameTextView)
        val emailTextView: TextView? = view?.findViewById(R.id.emailTextView)

        displayNameTextView?.text = userData.name.ifEmpty { "No Name" }
        emailTextView?.text = authViewModel.currentUser.value?.email

        val photoUrl = userData.photoUrl.takeIf { it.isNotEmpty() }
        if (photoUrl != null) {
            Picasso.get().load(photoUrl).into(userPhotoImageView)
        } else {
            userPhotoImageView.setImageResource(R.drawable.trainer_icon)
        }
    }

    override fun onDisplayNameUpdated(displayName: String) {
        if (displayName.isNotEmpty()) {
            profileViewModel.updateUserName(displayName)
            Log.d("NameUpdate", "Updated display name")
            Toast.makeText(requireContext(), "Username Updated!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Invalid username!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Profile Photo")
            .setMessage("Are you sure you want to delete your profile photo?")
            .setPositiveButton("Delete") { _, _ -> profileViewModel.deleteUserPhoto() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
