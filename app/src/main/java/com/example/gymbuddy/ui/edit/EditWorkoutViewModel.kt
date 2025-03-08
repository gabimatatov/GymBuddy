package com.example.gymbuddy.ui.edit

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbuddy.models.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.example.gymbuddy.repos.WorkoutRepository

class EditWorkoutViewModel : ViewModel() {

    private val workoutRepository = WorkoutRepository()

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun updateWorkout(
        workoutId: String,
        name: String,
        description: String,
        exercises: String,
        difficulty: String,
        imageBitmap: Bitmap?,
        existingImageUrl: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (imageBitmap != null) {
                    workoutRepository.uploadImage(imageBitmap,
                        onSuccess = { imageUrl ->
                            Model.shared.updateWorkout(workoutId, name, description, exercises, difficulty, imageUrl) { success ->
                                _updateSuccess.postValue(success)
                            }
                        },
                        onFailure = { error ->
                            _errorMessage.postValue("Image upload failed: ${error.message}")
                        }
                    )
                } else {
                    Model.shared.updateWorkout(workoutId, name, description, exercises, difficulty, existingImageUrl) { success ->
                        _updateSuccess.postValue(success)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error updating workout: ${e.message}")
            }
        }
    }

    fun deleteWorkoutImage(workoutId: String) {
        workoutRepository.deleteUserPhoto(workoutId, onSuccess = { }, onFailure = {})
    }
}
