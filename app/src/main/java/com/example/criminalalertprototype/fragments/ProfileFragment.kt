package com.example.criminalalertprototype.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.criminalalertprototype.R

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationGroup = view.findViewById<RadioGroup>(R.id.notification_group)
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