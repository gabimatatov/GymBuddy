package com.example.gymbuddy.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.gymbuddy.MainActivity
import com.example.gymbuddy.R
import com.example.gymbuddy.ViewModels.AuthViewModel
import com.example.gymbuddy.Objects.GlobalVariables
import com.example.gymbuddy.ViewModels.UserViewModel

class SignupActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val usernameEditText: EditText = findViewById(R.id.et_username_signup)
        val passwordEditText: EditText = findViewById(R.id.et_password_signup)
        val emailEditText: EditText = findViewById(R.id.et_email_signup)
        val signupButton: Button = findViewById(R.id.btn_signup)
        val signInTextView: TextView = findViewById(R.id.tv_signin)

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()

            // ✅ Input Validation Before Signup
            when {
                email.isEmpty() -> {
                    emailEditText.error = "Email is required"
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailEditText.error = "Enter a valid email address"
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    passwordEditText.error = "Password is required"
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    passwordEditText.error = "Password must be at least 6 characters"
                    return@setOnClickListener
                }
                username.isEmpty() -> {
                    usernameEditText.error = "Username is required"
                    return@setOnClickListener
                }
                else -> {
                    authViewModel.signUp(email, password, this)
                }
            }
        }

        // ✅ Observe Signup Errors and Show in Toast
        authViewModel.authError.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        // ✅ Navigate to LoginActivity
        signInTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // ✅ Observe Authentication State (Navigate to MainActivity if Signed In)
        authViewModel.isUserSignedIn.observe(this) { isSignedIn ->
            if (isSignedIn) {
                val username = usernameEditText.text.toString().trim()
                val userid = authViewModel.currentUser.value!!.uid
                val userViewModel = UserViewModel(userid)
                userViewModel.updateUserName(username)
                userViewModel.userLiveData.observe(this) { userdata ->
                    GlobalVariables.currentUser = userdata
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}
