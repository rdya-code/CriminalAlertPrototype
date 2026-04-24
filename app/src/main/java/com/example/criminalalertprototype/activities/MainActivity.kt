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
        
        userName = intent.getStringExtra("USER_NAME") ?: "User"
        
        setupViews()
        setupNavigation()
        
        // Listen for report submissions to refresh Dashboard
        supportFragmentManager.setFragmentResultListener("report_submitted", this) { _, _ ->
            refreshDashboard()
        }
        
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
        bottomNav.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }
                R.id.nav_report -> {
                    val reportFragment = ReportFragment()
                    val args = Bundle()
                    args.putString("USER_NAME", userName)
                    reportFragment.arguments = args
                    loadFragment(reportFragment)
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
    
    private fun refreshDashboard() {
        val dashboardFragment = DashboardFragment()
        val args = Bundle()
        args.putString("USER_NAME", userName)
        dashboardFragment.arguments = args
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, dashboardFragment)
            .commit()
        
        // Reset bottom nav selection
        bottomNav.selectedItemId = R.id.nav_dashboard
    }
}