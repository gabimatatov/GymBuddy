package com.example.gymbuddy.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbuddy.R
import com.example.gymbuddy.databinding.FragmentWorkoutDetailsBinding
import com.squareup.picasso.Picasso

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
        setupWorkoutImage()
        setupFavoriteButton()
        viewModel.checkIfUserIsOwner(args.workoutOwner)
        viewModel.checkIfFavorite(args.workoutId) // Check if the workout is already a favorite
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
            binding.buttonEditWorkout.visibility = if (isOwner) View.VISIBLE else View.GONE
        }

        binding.buttonDeleteWorkout.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.buttonEditWorkout.setOnClickListener {
            navigateToEditWorkout()
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().navigateUp()
            }
        }

        // Observe favorite status and update button text
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.buttonAddToFavorites.text =
                if (isFavorite) "Remove from Favorites" else "Add to Favorites"
        }

        viewModel.favoriteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Updated Favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupWorkoutImage() {
        val imageUrl = args.workoutImageUrl

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.gym_buddy_icon)
                .into(binding.imageWorkout)
        } else {
            binding.imageWorkout.setImageResource(R.drawable.gym_buddy_icon)
        }
    }

    private fun setupFavoriteButton() {
        binding.buttonAddToFavorites.setOnClickListener {
            viewModel.toggleFavorite(args.workoutId)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Workout")
            .setMessage("Are you sure you want to delete this workout?")
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteWorkout(args.workoutId) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun navigateToEditWorkout() {
        val action = WorkoutDetailsFragmentDirections
            .actionWorkoutDetailsFragmentToEditWorkoutFragment(
                workoutId = args.workoutId,
                workoutName = args.workoutName,
                workoutDescription = args.workoutDescription,
                workoutExercises = args.workoutExercises,
                workoutDifficulty = args.workoutDifficulty,
                workoutOwner = args.workoutOwner,
                workoutImageUrl = args.workoutImageUrl
            )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
