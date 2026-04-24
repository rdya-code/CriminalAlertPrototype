package com.example.criminalalertprototype.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.criminalalertprototype.R

class SettingsFragment : Fragment() {
    
    private lateinit var stealthSwitch: Switch
    private lateinit var notificationGroup: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupStealthMode(view)
        setupNotificationPreferences(view)
    }
    
    private fun setupStealthMode(view: View) {
        // Find switch inside the included layout
        val settingsRow = view.findViewById<View>(R.id.setting_row_container)
        stealthSwitch = settingsRow?.findViewById(R.id.stealth_switch) ?: return
        
        stealthSwitch.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Stealth Mode: ON - Vibrate only for high priority alerts"
                          else "Stealth Mode: OFF - All alerts will notify"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupNotificationPreferences(view: View) {
        notificationGroup = view.findViewById(R.id.notification_group)
        notificationGroup.setOnCheckedChangeListener { _, checkedId ->
            val message = when (checkedId) {
                R.id.radio_all -> "All alerts enabled"
                R.id.radio_high -> "High priority only"
                R.id.radio_none -> "Notifications disabled"
                else -> "Settings updated"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}