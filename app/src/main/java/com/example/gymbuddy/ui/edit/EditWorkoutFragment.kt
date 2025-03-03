package com.example.gymbuddy.ui.edit

import android.os.Bundle
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

class EditWorkoutFragment : Fragment() {

    private var _binding: FragmentEditWorkoutBinding? = null
    private val binding get() = _binding!!
    private val args: EditWorkoutFragmentArgs by navArgs()
    private val viewModel: EditWorkoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditWorkoutBinding.inflate(inflater, container, false)
        setupUI()
        setupObservers()
        return binding.root
    }

    private fun setupUI() {
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

    private fun saveWorkout() {
        val updatedName = binding.editTextWorkoutName.text.toString().trim()
        val updatedDescription = binding.editTextWorkoutDescription.text.toString().trim()
        val updatedExercises = binding.editTextWorkoutExercises.text.toString().trim()
        val updatedDifficulty = binding.spinnerDifficulty.text.toString().trim()

        if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedExercises.isEmpty()) {
            Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.updateWorkout(args.workoutId, updatedName, updatedDescription, updatedExercises, updatedDifficulty)
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
