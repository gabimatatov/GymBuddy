package com.example.gymbuddy

import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager

class AppConfiguration private constructor() {
    companion object {
        fun setFullScreen(window: Window){
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        }
    }
}