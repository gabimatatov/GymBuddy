package com.example.gymbuddy.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymbuddy.adapters.WorkoutAdapter
import com.example.gymbuddy.databinding.FragmentFavoritesBinding
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.activities.AuthViewModel

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var adapter: WorkoutAdapter
    private lateinit var authViewModel: AuthViewModel // Reference to AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize ViewModels
        favoritesViewModel = FavoritesViewModel()
        authViewModel = AuthViewModel() // Initialize AuthViewModel

        // Setup RecyclerView
        adapter = WorkoutAdapter(emptyList()) { workout ->
            // Handle click on favorite workout (e.g., open details)
        }
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = adapter

        // Observe AuthViewModel to get the current user
        authViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                // Pass the userId to the FavoritesViewModel to load the favorites
                favoritesViewModel.loadFavorites(user.uid ?: "")
            }
        })

        // Observe the favorite workouts data and update UI
        favoritesViewModel.favorites.observe(viewLifecycleOwner) { workouts ->
            adapter.updateData(workouts)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
