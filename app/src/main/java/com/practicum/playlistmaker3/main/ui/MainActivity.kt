package com.practicum.playlistmaker3.main.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker3.R

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavContainer = findViewById(R.id.bottomNavigationContainer)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerFragment,
                R.id.createPlaylistFragment,
                R.id.playlistFragment,
                R.id.editPlaylistFragment -> {
                    bottomNavContainer.visibility = View.GONE
                }
                else -> {
                    bottomNavContainer.visibility = View.VISIBLE
                }
            }
        }
    }
}