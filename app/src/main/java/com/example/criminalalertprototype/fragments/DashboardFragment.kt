package com.example.criminalalertprototype.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.adapters.AlertAdapter
import com.example.criminalalertprototype.models.AlertModel
import com.example.criminalalertprototype.utils.DataGenerator

class DashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlertAdapter
    private lateinit var searchEditText: EditText
    private var alertList: List<AlertModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setupSearch(view)
        loadAlerts()
    }

    // F3: RecyclerView with custom Adapter and ViewHolder
    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_alerts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AlertAdapter(emptyList()) { alert ->
            // Navigate to detail fragment with Bundle (F2)
            val detailFragment = CommunityDetailFragment.newInstance(alert)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter
    }

    // F5: Search/Filter feature for RecyclerView
    private fun setupSearch(view: View) {
        searchEditText = view.findViewById(R.id.search_alerts)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadAlerts() {
        alertList = DataGenerator.generateAlerts()
        adapter.updateData(alertList)
        updateStats()
    }

    private fun updateStats() {
        val activeAlerts = alertList.count { it.urgency == "High" }
        val view = view
        view?.findViewById<android.widget.TextView>(R.id.stats_alerts)?.text = activeAlerts.toString()
    }
}