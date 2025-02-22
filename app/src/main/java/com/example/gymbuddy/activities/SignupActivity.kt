package com.example.gymbuddy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gymbuddy.MainActivity
import com.example.gymbuddy.R
import com.example.gymbuddy.ViewModels.AuthViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Loading UI components
        val usernameEditText: EditText = findViewById(R.id.et_username_signup)
        val passwordEditText: EditText = findViewById(R.id.et_password_signup)
        val emailEditText: EditText = findViewById(R.id.et_email_signup)
        val signupButton: Button = findViewById(R.id.btn_signup)
        val signInTextView: TextView = findViewById(R.id.tv_signin)

        // Set onClickListener for the sign-up button
        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                authViewModel.signUp(email, password, username, this)
            }
        }

        // Navigate to the LoginActivity
        signInTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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