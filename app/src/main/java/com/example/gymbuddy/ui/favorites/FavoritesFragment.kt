package com.example.gymbuddy.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymbuddy.adapters.WorkoutAdapter
import com.example.gymbuddy.databinding.FragmentFavoritesBinding
import com.example.gymbuddy.activities.AuthViewModel
import com.example.gymbuddy.dataclass.Workout

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var adapter: WorkoutAdapter
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize ViewModels
        favoritesViewModel = FavoritesViewModel()
        authViewModel = AuthViewModel()

        // Setup RecyclerView
        adapter = WorkoutAdapter(emptyList()) { selectedWorkout ->
            navigateToWorkoutDetails(selectedWorkout)
        }
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = adapter

        // Observe AuthViewModel to get the current user
        authViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                // Calculate the 'since' timestamp (e.g., 24 hours ago)
                val sinceTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
                favoritesViewModel.loadFavorites(user.uid ?: "", sinceTimestamp)
            }
        })

        // Observe the favorite workouts data and update UI
        favoritesViewModel.favorites.observe(viewLifecycleOwner) { workouts ->
            adapter.updateData(workouts)
        }

        return root
    }

    private fun navigateToWorkoutDetails(workout: Workout) {
        val action = FavoritesFragmentDirections
            .actionNavigationFavoritesToWorkoutDetailsFragment(
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
