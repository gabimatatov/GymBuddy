package com.example.gymbuddy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.gymbuddy.MainActivity
import com.example.gymbuddy.R
import com.example.gymbuddy.ViewModels.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Loading UI components
        val usernameEditText: EditText = findViewById(R.id.et_username)
        val passwordEditText: EditText = findViewById(R.id.et_password)
        val loginButton: Button = findViewById(R.id.btn_login)
        val signUpTextView: TextView = findViewById(R.id.tv_signup)

        // Set onClickListener for the login button
        loginButton.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Call the signIn method in AuthViewModel
                authViewModel.signIn(email, password, this)
            }
        }

        // Navigate to the SignUpActivity
        signUpTextView.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        // Observe the isUserSignedIn LiveData to determine the authentication state
        authViewModel.isUserSignedIn.observe(this) { isSignedIn ->
            if (isSignedIn) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}