package com.example.gymbuddy.ui.home

import WorkoutAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymbuddy.R
import com.example.gymbuddy.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var workoutAdapter: WorkoutAdapter

    private val sharedPrefs by lazy {
        requireContext().getSharedPreferences("GymBuddyPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupDifficultyFilter()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        workoutAdapter = WorkoutAdapter(emptyList())
        binding.recyclerViewWorkouts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = workoutAdapter
        }
    }

    private fun setupDifficultyFilter() {
        val difficultyOptions = resources.getStringArray(R.array.difficulty_filter)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, difficultyOptions)

        binding.spinnerDifficultyFilter.setAdapter(adapter)

        val lastSelectedDifficulty = sharedPrefs.getString("selected_difficulty", "All Difficulties") ?: "All Difficulties"
        binding.spinnerDifficultyFilter.setText(lastSelectedDifficulty, false)

        viewModel.fetchWorkouts(if (lastSelectedDifficulty == "All Difficulties") null else lastSelectedDifficulty)

        binding.spinnerDifficultyFilter.setOnItemClickListener { _, _, position, _ ->
            val selectedDifficulty = difficultyOptions[position]
            sharedPrefs.edit().putString("selected_difficulty", selectedDifficulty).apply()
            viewModel.fetchWorkouts(if (selectedDifficulty == "All Difficulties") null else selectedDifficulty)
        }
    }

    private fun observeViewModel() {
        viewModel.workouts.observe(viewLifecycleOwner) { workouts ->
            workoutAdapter.updateData(workouts)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        viewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
