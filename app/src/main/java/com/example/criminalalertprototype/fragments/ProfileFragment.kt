package com.example.criminalalertprototype.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.adapters.AlertAdapter
import com.example.criminalalertprototype.database.PreferenceRepository
import com.example.criminalalertprototype.database.ReportRepository
import com.example.criminalalertprototype.models.AlertModel
import com.example.criminalalertprototype.models.ReportModel
import com.example.criminalalertprototype.models.UserModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvReportCount: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var stealthSwitch: Switch
    private lateinit var notificationGroup: RadioGroup
    private lateinit var btnDeleteAccount: Button
    
    private lateinit var reportRepository: ReportRepository
    private lateinit var preferenceRepository: PreferenceRepository
    private var userName: String = ""
    private var userReports: List<ReportModel> = emptyList()
    private lateinit var adapter: AlertAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        userName = arguments?.getString("USER_NAME") ?: "John"
        reportRepository = ReportRepository(requireContext())
        preferenceRepository = PreferenceRepository(requireContext())
        
        setupViews(view)
        loadUserData()
        loadUserReports()
        loadUserPreferences()
    }

    private fun setupViews(view: View) {
        tvUsername = view.findViewById(R.id.tv_profile_username)
        tvReportCount = view.findViewById(R.id.tv_report_count)
        recyclerView = view.findViewById(R.id.recycler_my_reports)
        stealthSwitch = view.findViewById(R.id.profile_stealth_switch)
        notificationGroup = view.findViewById(R.id.profile_notification_group)
        btnDeleteAccount = view.findViewById(R.id.btn_delete_account)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = AlertAdapter(emptyList()) { alert ->
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
            
            val detailFragment = CommunityDetailFragment.newInstance(report)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter
        
        // Setup listeners
        stealthSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                preferenceRepository.updateStealthMode(userName, isChecked)
                val message = if (isChecked) "Stealth Mode: ON" else "Stealth Mode: OFF"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
        
        notificationGroup.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.profile_radio_all -> "all"
                R.id.profile_radio_high -> "high_only"
                R.id.profile_radio_none -> "none"
                else -> "all"
            }
            lifecycleScope.launch {
                preferenceRepository.updateNotificationMode(userName, mode)
                Toast.makeText(context, "Notification preference saved", Toast.LENGTH_SHORT).show()
            }
        }
        
        btnDeleteAccount.setOnClickListener {
            deleteAllUserReports()
        }
    }

    private fun loadUserData() {
        tvUsername.text = userName
    }

    private fun loadUserReports() {
        lifecycleScope.launch {
            try {
                userReports = reportRepository.getAllReports(userName)
                tvReportCount.text = "📋 ${userReports.size} Total Reports"
                
                val alertModels = userReports.map { report ->
                    AlertModel(
                        id = report.id.toString(),
                        title = report.title,
                        description = report.description,
                        type = report.category,
                        timeAgo = report.createdAt.ifEmpty { "Recent" },
                        urgency = report.urgency,
                        location = report.location
                    )
                }
                adapter.updateData(alertModels)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading reports: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUserPreferences() {
        lifecycleScope.launch {
            try {
                val preferences = preferenceRepository.getUserPreferences(userName)
                if (preferences != null) {
                    stealthSwitch.isChecked = preferences.stealthMode
                    
                    when (preferences.notificationMode) {
                        "all" -> notificationGroup.check(R.id.profile_radio_all)
                        "high_only" -> notificationGroup.check(R.id.profile_radio_high)
                        "none" -> notificationGroup.check(R.id.profile_radio_none)
                    }
                }
            } catch (e: Exception) {
                // User might not have preferences yet - that's fine
            }
        }
    }

    private fun deleteAllUserReports() {
        lifecycleScope.launch {
            try {
                for (report in userReports) {
                    reportRepository.deleteReport(report.id)
                }
                loadUserReports()
                Toast.makeText(requireContext(), "All reports deleted", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error deleting reports: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserReports()
    }
}