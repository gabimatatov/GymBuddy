package com.example.gymbuddy.objects

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class CameraUtil(private val fragment: Fragment) {

    companion object {
        const val CAMERA_REQUEST_CODE = 1001
    }

    // Interface for camera result callback
    interface CameraResultCallback {
        fun onImageCaptured(bitmap: Bitmap)
    }

    private var cameraResultCallback: CameraResultCallback? = null

    // Permission launcher
    private val requestCameraPermission: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(
                    fragment.requireContext(),
                    "Camera permission is required!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Set the callback
    fun setCameraResultCallback(callback: CameraResultCallback) {
        this.cameraResultCallback = callback
    }

    // Check camera permission and open camera if granted
    fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    // Open the camera
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fragment.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    // Process the result
    fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val photo = data?.extras?.get("data") as Bitmap?
            photo?.let {
                cameraResultCallback?.onImageCaptured(it)
            }
        }
    }
}