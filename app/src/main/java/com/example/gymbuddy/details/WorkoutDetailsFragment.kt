package com.example.gymbuddy.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.gymbuddy.databinding.FragmentWorkoutDetailsBinding

class WorkoutDetailsFragment : Fragment() {

    private var _binding: FragmentWorkoutDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: WorkoutDetailsFragmentArgs by navArgs() // Retrieve passed workout data

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailsBinding.inflate(inflater, container, false)

        displayWorkoutDetails()

        return binding.root
    }

    private fun displayWorkoutDetails() {
        binding.textWorkoutName.text = args.workoutName
        binding.textWorkoutDescription.text = args.workoutDescription
        binding.textWorkoutExercises.text = args.workoutExercises
        binding.textWorkoutDifficulty.text = "Difficulty: ${args.workoutDifficulty}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
