package com.example.gymbuddy.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.gymbuddy.databinding.FragmentWorkoutDetailsBinding
import com.google.firebase.auth.FirebaseAuth

class WorkoutDetailsFragment : Fragment() {

    private var _binding: FragmentWorkoutDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: WorkoutDetailsFragmentArgs by navArgs()
    private val viewModel: WorkoutDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailsBinding.inflate(inflater, container, false)
        setupObservers()
        viewModel.checkIfUserIsOwner(args.workoutOwner)
        return binding.root
    }

    private fun setupObservers() {
        binding.textWorkoutName.text = args.workoutName
        binding.textWorkoutOwner.text = "Created by: ${args.workoutOwner}"
        binding.textWorkoutDescription.text = args.workoutDescription
        binding.textWorkoutExercises.text = args.workoutExercises
        binding.textWorkoutDifficulty.text = "Difficulty: ${args.workoutDifficulty}"

        viewModel.isOwner.observe(viewLifecycleOwner) { isOwner ->
            binding.buttonDeleteWorkout.visibility = if (isOwner) View.VISIBLE else View.GONE
        }

        binding.buttonDeleteWorkout.setOnClickListener {
            viewModel.deleteWorkout(args.workoutId)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
