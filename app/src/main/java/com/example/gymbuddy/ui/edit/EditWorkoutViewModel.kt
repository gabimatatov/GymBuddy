package com.example.gymbuddy.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.models.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditWorkoutViewModel : ViewModel() {

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun updateWorkout(
        workoutId: String, name: String, description: String, exercises: String, difficulty: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Model.shared.updateWorkout(workoutId, name, description, exercises, difficulty) { success ->
                    _updateSuccess.postValue(success)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error updating workout: ${e.message}")
            }
        }
    }
}
