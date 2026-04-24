package com.example.criminalalertprototype.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.adapters.AlertAdapter
import com.example.criminalalertprototype.database.BookmarkRepository
import com.example.criminalalertprototype.database.ReportRepository
import com.example.criminalalertprototype.models.AlertModel
import com.example.criminalalertprototype.models.ReportModel
import kotlinx.coroutines.launch

class BookmarksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: AlertAdapter
    private lateinit var bookmarkRepository: BookmarkRepository
    private lateinit var reportRepository: ReportRepository
    private var userName: String = ""
    private var bookmarkedReports: List<ReportModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmarks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        userName = arguments?.getString("USER_NAME") ?: "John"
        bookmarkRepository = BookmarkRepository(requireContext())
        reportRepository = ReportRepository(requireContext())
        
        setupViews(view)
        loadBookmarkedReports()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recycler_bookmarks)
        tvEmpty = view.findViewById(R.id.tv_empty_bookmarks)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = AlertAdapter(emptyList()) { alert ->
            // Navigate to detail fragment
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
    }

    private fun loadBookmarkedReports() {
        lifecycleScope.launch {
            try {
                bookmarkedReports = bookmarkRepository.getAllBookmarkedReports(userName)
                
                if (bookmarkedReports.isNotEmpty()) {
                    val alertModels = bookmarkedReports.map { report ->
                        AlertModel(
                            id = report.id.toString(),
                            title = report.title,
                            description = report.description,
                            type = report.category,
                            timeAgo = "Saved",
                            urgency = report.urgency,
                            location = report.location
                        )
                    }
                    adapter.updateData(alertModels)
                    recyclerView.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                } else {
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading bookmarks: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadBookmarkedReports()
    }
}