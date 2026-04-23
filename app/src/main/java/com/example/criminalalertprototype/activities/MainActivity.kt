package com.example.criminalalertprototype.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var tvWelcome: TextView
    private var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_container)

        // F1: Receive data from Intent Extras
        userName = intent.getStringExtra("USER_NAME") ?: "User"

        setupViews()
        setupNavigation()

        // Load default fragment (Dashboard)
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }
    }

    private fun setupViews() {
        bottomNav = findViewById(R.id.bottom_navigation)
        tvWelcome = findViewById(R.id.tv_welcome)
        tvWelcome.text = "Welcome back, $userName!"
    }

    private fun setupNavigation() {
        // F4: Fragment Transactions - Switch between fragments without restarting activity
        bottomNav.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }
                R.id.nav_report -> {
                    loadFragment(ReportFragment())
                    true
                }
                R.id.nav_alerts -> {
                    loadFragment(AlertsFragment())
                    true
                }
                R.id.nav_community -> {
                    loadFragment(CommunityFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}