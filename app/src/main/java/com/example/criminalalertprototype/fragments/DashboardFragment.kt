package com.example.criminalalertprototype.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.adapters.AlertAdapter
import com.example.criminalalertprototype.database.ReportRepository
import com.example.criminalalertprototype.models.AlertModel
import com.example.criminalalertprototype.models.ReportModel
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlertAdapter
    private lateinit var searchEditText: EditText
    private lateinit var statsAlerts: TextView
    private lateinit var zoneStatus: TextView
    private lateinit var statsPatrols: TextView
    private lateinit var tvEmptyState: TextView
    private lateinit var reportRepository: ReportRepository
    private var alertList: List<AlertModel> = emptyList()
    private var userName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get username from arguments or shared preferences
        userName = arguments?.getString("USER_NAME") ?: "John"
        
        // Initialize repository
        reportRepository = ReportRepository(requireContext())
        
        setupViews(view)
        setupRecyclerView(view)
        setupSearch(view)
        loadAlertsFromDatabase()
    }

    private fun setupViews(view: View) {
        statsAlerts = view.findViewById(R.id.stats_alerts)
        zoneStatus = view.findViewById(R.id.zone_status)
        statsPatrols = view.findViewById(R.id.stats_patrols)
        tvEmptyState = view.findViewById(R.id.tv_empty_state)
        
        // Set static values for now
        zoneStatus.text = "Safe"
        statsPatrols.text = "4"
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_alerts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = AlertAdapter(emptyList()) { alert ->
            // Convert AlertModel to ReportModel for database
            val report = ReportModel(
                id = alert.id.toIntOrNull() ?: 0,
                title = alert.title,
                description = alert.description,
                category = alert.type,
                urgency = alert.urgency,
                location = alert.location,
                status = "active",
                userName = userName
            )
            
            // Navigate to detail fragment with Bundle
            val detailFragment = CommunityDetailFragment.newInstance(report)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter
    }

    private fun setupSearch(view: View) {
        searchEditText = view.findViewById(R.id.search_alerts)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadAlertsFromDatabase() {
        lifecycleScope.launch {
            try {
                val reports = reportRepository.getAllActiveReports(userName)
                
                if (reports.isNotEmpty()) {
                    updateUIWithReports(reports)
                    showRecyclerView()
                } else {
                    showEmptyState()
                }
                updateActiveAlertsCount()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error loading alerts: ${e.message}", Toast.LENGTH_SHORT).show()
                showEmptyState()
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            loadAlertsFromDatabase()
        } else {
            lifecycleScope.launch {
                try {
                    val searchResults = reportRepository.searchReports(userName, query)
                    val alertModels = searchResults.map { report ->
                        AlertModel(
                            id = report.id.toString(),
                            title = report.title,
                            description = report.description,
                            type = report.category,
                            timeAgo = formatTimeAgo(report.createdAt),
                            urgency = report.urgency,
                            location = report.location
                        )
                    }
                    
                    if (alertModels.isNotEmpty()) {
                        adapter.updateData(alertModels)
                        showRecyclerView()
                    } else {
                        showEmptyState()
                        Toast.makeText(requireContext(), "No results found for '$query'", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Search error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUIWithReports(reports: List<ReportModel>) {
        alertList = reports.map { report ->
            AlertModel(
                id = report.id.toString(),
                title = report.title,
                description = report.description,
                type = report.category,
                timeAgo = formatTimeAgo(report.createdAt),
                urgency = report.urgency,
                location = report.location
            )
        }
        adapter.updateData(alertList)
    }

    private fun updateActiveAlertsCount() {
        lifecycleScope.launch {
            try {
                val count = reportRepository.getActiveAlertsCount(userName)
                statsAlerts.text = count.toString()
            } catch (e: Exception) {
                statsAlerts.text = "0"
            }
        }
    }

    private fun formatTimeAgo(dateTimeString: String): String {
        if (dateTimeString.isEmpty()) return "Just now"
        
        return try {
            // Simple formatting - you can enhance this
            "Recently"
        } catch (e: Exception) {
            "Just now"
        }
    }

    private fun showEmptyState() {
        recyclerView.visibility = View.GONE
        if (::tvEmptyState.isInitialized) {
            tvEmptyState.visibility = View.VISIBLE
            tvEmptyState.text = "No active alerts\n\nSubmit a report to see it here"
        }
    }

    private fun showRecyclerView() {
        recyclerView.visibility = View.VISIBLE
        if (::tvEmptyState.isInitialized) {
            tvEmptyState.visibility = View.GONE
        }
    }

    // Refresh dashboard when coming back from other fragments
    override fun onResume() {
        super.onResume()
        loadAlertsFromDatabase()
    }
}