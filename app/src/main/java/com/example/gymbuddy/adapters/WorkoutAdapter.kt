package com.example.gymbuddy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbuddy.databinding.ItemWorkoutBinding
import com.example.gymbuddy.dataclass.Workout
import com.squareup.picasso.Picasso
import com.example.gymbuddy.R

class WorkoutAdapter(
    private var workouts: List<Workout>,
    private val onWorkoutClick: (Workout) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(private val binding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workout: Workout, onWorkoutClick: (Workout) -> Unit) {
            binding.textWorkoutName.text = workout.name
            binding.textWorkoutDescription.text = workout.description
            binding.textWorkoutDifficulty.text = "Difficulty: ${workout.difficulty}"
            binding.textWorkoutCreator.text = "Created by: ${workout.ownerId}"

            // Load image with Picasso
            if (!workout.imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(workout.imageUrl)
                    .placeholder(R.drawable.gym_buddy_icon)
                    .into(binding.imageWorkout)
            } else {
                binding.imageWorkout.setImageResource(R.drawable.gym_buddy_icon)
            }
            binding.root.setOnClickListener {
                onWorkoutClick(workout)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(workouts[position], onWorkoutClick)
    }

    override fun getItemCount(): Int = workouts.size

    fun updateData(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged()
    }
}
