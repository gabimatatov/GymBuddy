package com.example.gymbuddy.objects

import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object GalleryHandler {
    const val REQUEST_CODE_PICK_IMAGE = 123

    // Public function to get photo URI from the gallery
    fun getPhotoUriFromGallery(
        activity: FragmentActivity,
        pickImageLauncher: ActivityResultLauncher<Intent>,
        requestPermissionLauncher: ActivityResultLauncher<String>?
    ) {
        if (checkPermissions(activity)) {
            openGallery(activity, pickImageLauncher)
        } else {
            requestPermissionLauncher?.let{requestPermissions(activity, requestPermissionLauncher)}
        }
    }

    private fun checkPermissions(activity: FragmentActivity): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return readPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(
        activity: FragmentActivity,
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun openGallery(
        activity: FragmentActivity,
        pickImageLauncher: ActivityResultLauncher<Intent>
    ) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }
}