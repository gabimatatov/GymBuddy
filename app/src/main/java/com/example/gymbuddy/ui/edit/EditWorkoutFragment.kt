package com.example.gymbuddy.ui.edit

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbuddy.R
import com.example.gymbuddy.databinding.FragmentEditWorkoutBinding
import com.example.gymbuddy.objects.CameraUtil
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.UUID

class EditWorkoutFragment : Fragment(), CameraUtil.CameraResultCallback {

    private var _binding: FragmentEditWorkoutBinding? = null
    private val binding get() = _binding!!
    private val args: EditWorkoutFragmentArgs by navArgs()
    private val viewModel: EditWorkoutViewModel by viewModels()

    // Camera utility
    private lateinit var cameraUtil: CameraUtil

    // Temporary bitmap to store captured image
    private var capturedImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize CameraUtil
        cameraUtil = CameraUtil(this)
        cameraUtil.setCameraResultCallback(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditWorkoutBinding.inflate(inflater, container, false)
        setupUI()
        setupObservers()
        return binding.root
    }

    private fun setupUI() {
        // Setup workout image with camera intent
        setupWorkoutImage()

        binding.editTextWorkoutName.setText(args.workoutName)
        binding.editTextWorkoutDescription.setText(args.workoutDescription)
        binding.editTextWorkoutExercises.setText(args.workoutExercises)

        val difficultyOptions = resources.getStringArray(R.array.difficulty_selection)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, difficultyOptions)
        binding.spinnerDifficulty.setAdapter(adapter)
        binding.spinnerDifficulty.setText(args.workoutDifficulty, false)

        binding.buttonSaveWorkout.setOnClickListener {
            saveWorkout()
        }
    }

    private fun setupWorkoutImage() {
        // Get the image URL from SafeArgs
        val imageUrl = args.workoutImageUrl

        // Set click listener to open camera
        binding.imageWorkout.setOnClickListener {
            cameraUtil.checkCameraPermission()
        }

        // Load image with Picasso
        if (!imageUrl.isNullOrEmpty()) {
            Log.d("test", "enterd")
            Picasso.get()
                .load(imageUrl)
                .into(binding.imageWorkout)
        } else {
            Log.d("test", "enterssssssd")
            // Set default image if no URL is provided
            binding.imageWorkout.setImageResource(R.drawable.gym_buddy_icon)
        }
    }

    // Implement the camera result callback
    override fun onImageCaptured(bitmap: Bitmap) {
        // Store the captured bitmap
        capturedImageBitmap = bitmap

        // Display the captured image
        binding.imageWorkout.setImageBitmap(bitmap)
    }

    private fun saveWorkout() {
        val updatedName = binding.editTextWorkoutName.text.toString().trim()
        val updatedDescription = binding.editTextWorkoutDescription.text.toString().trim()
        val updatedExercises = binding.editTextWorkoutExercises.text.toString().trim()
        val updatedDifficulty = binding.spinnerDifficulty.text.toString().trim()

        if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedExercises.isEmpty()) {
            Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        // If an image was captured, upload it first
        capturedImageBitmap?.let { bitmap ->
            uploadImageToFirebase(bitmap) { imageUrl ->
                Log.d("test", "${imageUrl}")
                // Update workout with the new image URL
                viewModel.updateWorkout(
                    args.workoutId,
                    updatedName,
                    updatedDescription,
                    updatedExercises,
                    updatedDifficulty,
                    imageUrl
                )
            }
        } ?: run {
            // If no new image, update workout with existing image URL
            Log.d("test", "Image is empty")
            viewModel.updateWorkout(
                args.workoutId,
                updatedName,
                updatedDescription,
                updatedExercises,
                updatedDifficulty,
                args.workoutImageUrl
            )
        }
    }

    // Function to upload image to Firebase Storage
    private fun uploadImageToFirebase(bitmap: Bitmap, onSuccess: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        // Generate a unique filename
        val filename = "workout_${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child("workout_images/$filename")

        // Convert bitmap to byte array
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Upload image
        imageRef.putBytes(data)
            .addOnSuccessListener { taskSnapshot ->
                // Get download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Image upload failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // Override onActivityResult to process camera intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        cameraUtil.processActivityResult(requestCode, resultCode, data)
    }

    private fun setupObservers() {
        viewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Workout updated successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.navigation_home)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}