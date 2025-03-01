package com.example.gymbuddy.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.gymbuddy.R
import com.example.gymbuddy.databinding.FragmentAddBinding
import com.google.android.material.textfield.TextInputEditText

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddViewModel by viewModels()
    private var selectedDifficulty: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        setupDifficultyDropdown()
        observeViewModel()

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

        // Get the AutoCompleteTextView from the TextInputLayout
        val difficultyDropdown = binding.spinnerDifficulty
        difficultyDropdown.setAdapter(dropdownAdapter)

        // Set default selection to first item
        if (difficultyOptions.isNotEmpty()) {
            selectedDifficulty = difficultyOptions[0]
            difficultyDropdown.setText(selectedDifficulty, false)
        }

        // Handle selection
        difficultyDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedDifficulty = difficultyOptions[position]
        }
    }

    private fun saveWorkout() {
        val name = binding.editTextWorkoutName.text.toString().trim()
        val description = binding.editTextWorkoutDescription.text.toString().trim()
        val exercises = binding.editTextWorkoutExercises.text.toString().trim()

        // Use the selected difficulty from the AutoCompleteTextView
        viewModel.saveWorkout(name, description, exercises, selectedDifficulty)
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
}