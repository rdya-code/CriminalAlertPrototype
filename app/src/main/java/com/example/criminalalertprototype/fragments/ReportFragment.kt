package com.example.criminalalertprototype.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.database.ReportRepository
import com.example.criminalalertprototype.models.ReportModel
import kotlinx.coroutines.launch

class ReportFragment : Fragment() {

    private lateinit var urgencyGroup: RadioGroup
    private lateinit var descriptionEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var reportRepository: ReportRepository
    private var selectedCategory: String = "Theft"
    private var selectedUrgency: String = "Low"
    private var userName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        userName = arguments?.getString("USER_NAME") ?: "John"
        reportRepository = ReportRepository(requireContext())
        
        setupViews(view)
        setupCategorySelection(view)
        setupUrgencySelection(view)
        setupSubmitButton(view)
    }

    private fun setupViews(view: View) {
        progressBar = view.findViewById(R.id.progress_bar_report)
        if (progressBar != null) {
            progressBar.visibility = View.GONE
        }
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
            val description = descriptionEditText.text.toString().trim()
            val location = "Current Location"
            
            if (description.isNotBlank()) {
                saveReportToDatabase(description, location)
            } else {
                Toast.makeText(context, "Please enter a description", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveReportToDatabase(description: String, location: String) {
        submitButton.isEnabled = false
        if (::progressBar.isInitialized) {
            progressBar.visibility = View.VISIBLE
        }
        
        lifecycleScope.launch {
            try {
                val report = ReportModel(
                    id = 0,
                    title = selectedCategory,
                    description = description,
                    category = selectedCategory,
                    urgency = selectedUrgency,
                    location = location,
                    status = "active",
                    userName = userName
                )
                
                val result = reportRepository.insertReport(report)
                
                if (result != -1L) {
                    Toast.makeText(context, "Report submitted successfully!", Toast.LENGTH_SHORT).show()
                    descriptionEditText.text.clear()
                    
                    // Send result back to Dashboard
                    val bundle = Bundle()
                    bundle.putBoolean("report_submitted", true)
                    parentFragmentManager.setFragmentResult("report_submitted", bundle)
                    
                    // Navigate back to Dashboard
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(context, "Failed to submit report", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } finally {
                submitButton.isEnabled = true
                if (::progressBar.isInitialized) {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
}