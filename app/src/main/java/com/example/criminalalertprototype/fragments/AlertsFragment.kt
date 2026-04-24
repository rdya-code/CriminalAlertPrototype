package com.example.criminalalertprototype.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.adapters.NewsAdapter
import com.example.criminalalertprototype.api.ApiService
import com.example.criminalalertprototype.api.Article
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlertsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: NewsAdapter
    private var newsList: List<Article> = emptyList()

    // Get your FREE API key from https://newsapi.org/register
    // Replace this with your actual API key
    private val API_KEY = "8095ad55d0824c868d5c78949296ad59"
    private val BASE_URL = "https://newsapi.org/"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alerts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        fetchCrimeNews()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recycler_news)
        progressBar = view.findViewById(R.id.progress_bar)
        tvEmpty = view.findViewById(R.id.tv_empty)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NewsAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    private fun fetchCrimeNews() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvEmpty.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(ApiService::class.java)
                
                // Search for crime-related news in Pakistan
                val query = "crime OR robbery OR theft OR murder Pakistan"
                
                val response = apiService.getCrimeNews(
                    query = query,
                    apiKey = API_KEY
                )

                progressBar.visibility = View.GONE

                if (response.status == "ok" && response.articles.isNotEmpty()) {
                    newsList = response.articles
                    adapter.updateData(newsList)
                    recyclerView.visibility = View.VISIBLE
                } else {
                    tvEmpty.visibility = View.VISIBLE
                    tvEmpty.text = "No crime news found.\nTry again later."
                }

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                tvEmpty.visibility = View.VISIBLE
                tvEmpty.text = "Error loading news: ${e.message}\n\nPlease check your internet connection."
                Toast.makeText(requireContext(), "Failed to load news: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}