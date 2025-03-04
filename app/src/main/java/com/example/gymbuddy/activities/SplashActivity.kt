package com.example.gymbuddy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.example.gymbuddy.MainActivity
import com.example.gymbuddy.R
import com.example.gymbuddy.objects.GlobalVariables
import com.example.gymbuddy.ui.profile.ProfileViewModel

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
                val userViewModel = ProfileViewModel(userid)
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