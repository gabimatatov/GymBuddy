package com.example.gymbuddy.ui.add

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.gymbuddy.R
import com.example.gymbuddy.databinding.FragmentAddBinding
import com.example.gymbuddy.objects.CameraUtil

class AddFragment : Fragment(), CameraUtil.CameraResultCallback {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddViewModel by viewModels()
    private var selectedDifficulty: String = ""

    // Camera utility
    private lateinit var cameraUtil: CameraUtil
    private var capturedImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraUtil = CameraUtil(this)
        cameraUtil.setCameraResultCallback(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        setupDifficultyDropdown()
        observeViewModel()
        setupImage()

        binding.buttonSaveWorkout.setOnClickListener {
            saveWorkout()
        }
        return binding.root
    }

    private fun setupDifficultyDropdown() {
        val difficultyOptions = resources.getStringArray(R.array.difficulty_selection)
        val dropdownAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            difficultyOptions
        )

        val difficultyDropdown = binding.spinnerDifficulty
        difficultyDropdown.setAdapter(dropdownAdapter)

        if (difficultyOptions.isNotEmpty()) {
            selectedDifficulty = difficultyOptions[0]
            difficultyDropdown.setText(selectedDifficulty, false)
        }

        difficultyDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedDifficulty = difficultyOptions[position]
        }
    }

    private fun saveWorkout() {
        val name = binding.editTextWorkoutName.text.toString().trim()
        val description = binding.editTextWorkoutDescription.text.toString().trim()
        val exercises = binding.editTextWorkoutExercises.text.toString().trim()

        viewModel.saveWorkout(
            name,
            description,
            exercises,
            selectedDifficulty,
            capturedImageBitmap
        )
    }

    private fun observeViewModel() {
        viewModel.workoutSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                Toast.makeText(requireContext(), "Workout saved!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_navigation_add_to_navigation_home)
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

    private fun updateImageTrashButtonVisibility() {
        // Show trash button only if we have a captured image
        binding.buttonClearImage.visibility = if (capturedImageBitmap != null) View.VISIBLE else View.GONE
    }

    private fun setupImage() {
        binding.imageWorkout.setImageResource(R.drawable.gym_buddy_icon)
        binding.imageWorkout.setOnClickListener {
            cameraUtil.checkCameraPermission()
        }

        // Set up the clear image button
        binding.buttonClearImage.setOnClickListener {
            // Reset to default image
            binding.imageWorkout.setImageResource(R.drawable.gym_buddy_icon)
            capturedImageBitmap = null
            updateImageTrashButtonVisibility()
        }

        updateImageTrashButtonVisibility()
    }

    // Override onActivityResult to process camera intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        cameraUtil.processActivityResult(requestCode, resultCode, data)
    }

    // Implement the camera result callback
    override fun onImageCaptured(bitmap: Bitmap) {
        // Store the captured bitmap
        capturedImageBitmap = bitmap

        // Display the captured image
        binding.imageWorkout.setImageBitmap(bitmap)

        updateImageTrashButtonVisibility()
    }
}
