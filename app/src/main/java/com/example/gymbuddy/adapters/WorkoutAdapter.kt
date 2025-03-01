import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbuddy.databinding.ItemWorkoutBinding
import com.example.gymbuddy.dataclass.Workout
import com.example.gymbuddy.ui.home.HomeFragmentDirections

class WorkoutAdapter(private var workouts: List<Workout>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(private val binding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workout: Workout) {
            binding.textWorkoutName.text = workout.name
            binding.textWorkoutDescription.text = workout.description
            binding.textWorkoutDifficulty.text = "Difficulty: ${workout.difficulty}"
            binding.textWorkoutCreator.text = "Created by: ${workout.ownerId}"

            binding.root.setOnClickListener {
                val action = HomeFragmentDirections
                    .actionNavigationHomeToWorkoutDetailsFragment(
                        workout.name,
                        workout.description,
                        workout.exercises,
                        workout.difficulty
                    )
                it.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(workouts[position])
    }

    override fun getItemCount(): Int = workouts.size

    fun updateData(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged()
    }
}
