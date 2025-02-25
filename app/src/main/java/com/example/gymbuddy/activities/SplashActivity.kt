package com.example.gymbuddy.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.example.gymbuddy.MainActivity
import com.example.gymbuddy.R
import com.example.gymbuddy.ViewModels.AuthViewModel
import com.example.gymbuddy.AppConfiguration
import com.example.gymbuddy.GlobalVariables
import com.example.gymbuddy.ViewModels.UserViewModel

class SplashActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 1000)
    }
    private fun checkUserStatus() {
        authViewModel.isUserSignedIn.observe(this) { isSignedIn ->
            if (isSignedIn) {
                // User is signed in, navigate to MainActivity
                val userid = authViewModel.currentUser.value!!.uid
                val userViewModel = UserViewModel(userid)
                userViewModel.userLiveData.observe(this) { userdata ->
                    GlobalVariables.currentUser = userdata
                    // User is signed in, navigate to the MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            } else {
                // User is not signed in, navigate to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}