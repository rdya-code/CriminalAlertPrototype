package com.example.criminalalertprototype.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.database.ReportRepository
import com.example.criminalalertprototype.models.ReportModel
import kotlinx.coroutines.launch

class CommunityDetailFragment : Fragment() {

    companion object {
        private const val ARG_REPORT = "report_data"
        
        fun newInstance(report: ReportModel): CommunityDetailFragment {
            val fragment = CommunityDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_REPORT, report)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var report: ReportModel
    private lateinit var reportRepository: ReportRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        report = arguments?.getParcelable(ARG_REPORT) 
            ?: throw IllegalArgumentException("Report data required")
        reportRepository = ReportRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        displayReportDetails(view)
        setupButtons(view)
    }

    private fun displayReportDetails(view: View) {
        view.findViewById<TextView>(R.id.detail_title).text = report.title
        view.findViewById<TextView>(R.id.detail_description).text = report.description
        view.findViewById<TextView>(R.id.detail_type).text = report.category
        view.findViewById<TextView>(R.id.detail_location).text = report.location
        view.findViewById<TextView>(R.id.detail_urgency).text = "Urgency: ${report.urgency}"
        
        // Set urgency color
        val urgencyText = view.findViewById<TextView>(R.id.detail_urgency)
        when (report.urgency) {
            "High" -> urgencyText.setTextColor(resources.getColor(R.color.alert_red))
            "Medium" -> urgencyText.setTextColor(resources.getColor(R.color.warning_orange))
            else -> urgencyText.setTextColor(resources.getColor(R.color.safety_blue))
        }
        
        // Show time if available
        if (report.createdAt.isNotEmpty()) {
            view.findViewById<TextView>(R.id.detail_time).text = report.createdAt
        } else {
            view.findViewById<TextView>(R.id.detail_time).visibility = View.GONE
        }
    }

    private fun setupButtons(view: View) {
        view.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            updateReportStatus("confirmed")
        }
        
        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            updateReportStatus("dismissed")
        }
    }

    private fun updateReportStatus(newStatus: String) {
        lifecycleScope.launch {
            try {
                val result = reportRepository.updateReportStatus(report.id, newStatus)
                
                if (result > 0) {
                    val message = if (newStatus == "confirmed") {
                        "Alert confirmed! Thanks for your feedback."
                    } else {
                        "Alert dismissed."
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}