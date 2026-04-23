package com.example.criminalalertprototype.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.models.AlertModel
import java.util.UUID

class ReportFragment : Fragment() {

    private lateinit var urgencyGroup: RadioGroup
    private lateinit var descriptionEditText: EditText
    private lateinit var submitButton: Button
    private var selectedCategory: String = "Theft"
    private var selectedUrgency: String = "Low"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySelection(view)
        setupUrgencySelection(view)
        setupSubmitButton(view)
    }

    private fun setupCategorySelection(view: View) {
        val theftBtn = view.findViewById<Button>(R.id.btn_theft)
        val vandalismBtn = view.findViewById<Button>(R.id.btn_vandalism)
        val fireBtn = view.findViewById<Button>(R.id.btn_fire)
        val suspiciousBtn = view.findViewById<Button>(R.id.btn_suspicious)
        val emergencyBtn = view.findViewById<Button>(R.id.btn_emergency)

        theftBtn?.setOnClickListener {
            selectedCategory = "Theft"
            Toast.makeText(context, "Category: Theft", Toast.LENGTH_SHORT).show()
        }
        vandalismBtn?.setOnClickListener {
            selectedCategory = "Vandalism"
            Toast.makeText(context, "Category: Vandalism", Toast.LENGTH_SHORT).show()
        }
        fireBtn?.setOnClickListener {
            selectedCategory = "Fire"
            Toast.makeText(context, "Category: Fire", Toast.LENGTH_SHORT).show()
        }
        suspiciousBtn?.setOnClickListener {
            selectedCategory = "Suspicious"
            Toast.makeText(context, "Category: Suspicious Activity", Toast.LENGTH_SHORT).show()
        }
        emergencyBtn?.setOnClickListener {
            Toast.makeText(context, "EMERGENCY! Call 911 now!", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupUrgencySelection(view: View) {
        urgencyGroup = view.findViewById(R.id.urgencyGroup)
        urgencyGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedUrgency = when (checkedId) {
                R.id.radioLow -> "Low"
                R.id.radioMedium -> "Medium"
                R.id.radioHigh -> "High"
                else -> "Low"
            }
        }
    }

    private fun setupSubmitButton(view: View) {
        descriptionEditText = view.findViewById(R.id.et_description)
        submitButton = view.findViewById(R.id.btn_submit_report)

        submitButton.setOnClickListener {
            val description = descriptionEditText.text.toString()
            if (description.isNotBlank()) {
                val newAlert = AlertModel(
                    id = UUID.randomUUID().toString(),
                    title = selectedCategory,
                    description = description,
                    type = selectedCategory,
                    timeAgo = "Just now",
                    urgency = selectedUrgency,
                    location = "Current Location"
                )

                // Pass data back via Bundle
                val result = Bundle()
                result.putParcelable("new_alert", newAlert)
                parentFragmentManager.setFragmentResult("new_alert_request", result)

                Toast.makeText(context, "Report submitted successfully!", Toast.LENGTH_SHORT).show()
                descriptionEditText.text.clear()
            } else {
                Toast.makeText(context, "Please enter a description", Toast.LENGTH_SHORT).show()
            }
        }
    }
}