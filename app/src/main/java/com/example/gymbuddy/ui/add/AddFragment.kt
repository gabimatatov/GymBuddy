package com.example.gymbuddy.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.databinding.FragmentAddBinding
import com.example.gymbuddy.repos.WorkoutRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val workoutRepository = WorkoutRepository()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() } // Get FirebaseAuth instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        binding.buttonSaveWorkout.setOnClickListener {
            saveWorkout()
        }

        return binding.root
    }

    private fun saveWorkout() {
        val name = binding.editTextWorkoutName.text.toString().trim()
        val description = binding.editTextWorkoutDescription.text.toString().trim()
        val difficulty = binding.editTextDifficulty.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || difficulty.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.currentUser?.email?.let { email ->
            val workout = Workout(
                workoutId = UUID.randomUUID().toString(),
                name = name,
                description = description,
                imageUrl = "", // Can be updated later with an image upload feature
                exercises = emptyList(),
                ownerId = email,
                difficulty = difficulty
            )

            workoutRepository.addWorkout(workout,
                onSuccess = {
                    Toast.makeText(requireContext(), "Workout saved!", Toast.LENGTH_SHORT).show()
                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } ?: Toast.makeText(requireContext(), "Error: User email not found!", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
