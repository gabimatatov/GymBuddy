package com.example.gymbuddy.ui.edit

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbuddy.R
import com.example.gymbuddy.databinding.FragmentEditWorkoutBinding
import com.example.gymbuddy.objects.CameraUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso

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
        // Setup workout image with camera intent and trash icon
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

        // Setup trash icon click listener
        binding.buttonClearImage.setOnClickListener {
            showDeleteImageConfirmationDialog()
        }

        // Load image with Picasso
        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(imageUrl)
                .into(binding.imageWorkout)
            binding.buttonClearImage.visibility = View.VISIBLE
        } else {
            binding.imageWorkout.setImageResource(R.drawable.gym_buddy_icon)
            binding.buttonClearImage.visibility = View.GONE
        }
    }

    private fun showDeleteImageConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Image")
            .setMessage("Are you sure you want to delete this photo?")
            .setPositiveButton("Yes") { _, _ ->
               deleteWorkoutImage()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteWorkoutImage() {
        // Reset image to default
        binding.imageWorkout.setImageResource(R.drawable.gym_buddy_icon)
        capturedImageBitmap = null

        // Tell the ViewModel to delete the image from storage
        if (!args.workoutImageUrl.isNullOrEmpty()) {
            viewModel.deleteWorkoutImage(args.workoutId)
        }

        // Hide the trash button
        binding.buttonClearImage.visibility = View.GONE
    }

    // Implement the camera result callback
    override fun onImageCaptured(bitmap: Bitmap) {
        // Store the captured bitmap
        capturedImageBitmap = bitmap

        // Display the captured image
        binding.imageWorkout.setImageBitmap(bitmap)

        // Show the trash button
        binding.buttonClearImage.visibility = View.VISIBLE
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

        viewModel.updateWorkout(
            args.workoutId,
            updatedName,
            updatedDescription,
            updatedExercises,
            updatedDifficulty,
            capturedImageBitmap,
            args.workoutImageUrl
        )
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

        viewModel.imageDeleted.observe(viewLifecycleOwner) { deleted ->
            if (deleted) {
                Toast.makeText(requireContext(), "Image deleted successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}