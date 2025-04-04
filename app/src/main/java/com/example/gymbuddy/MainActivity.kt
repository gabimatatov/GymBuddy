package com.example.gymbuddy

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.gymbuddy.activities.AuthViewModel
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gymbuddy.activities.LoginActivity
import com.example.gymbuddy.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUserStatus()
        initNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                // Handle sign out action here
                authViewModel.signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkUserStatus() {
        authViewModel.isUserSignedIn.observe(this) { isSignedIn ->
            if (!isSignedIn) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun initNavigation() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Set up Navigation component
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up ActionBar with NavController
        setupActionBarWithNavController(navController)

        // Set up Bottom Navigation View
        binding.bottomNavigationView.setupWithNavController(navController)

        // Handle item selection manually
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home,
                R.id.navigation_add,
                R.id.navigation_chat,
                R.id.navigation_favorites,
                R.id.navigation_profile -> {
                    // Check if the selected destination is different from the current one
                    if (binding.bottomNavigationView.selectedItemId != item.itemId) {
                        navController.navigate(item.itemId)
//                        navController.popBackStack(
//                            R.id.navigation_profile,
//                            item.itemId == R.id.navigation_profile
//                        )
                    }
                    true
                }
                else -> false
            }
        }
    }
}