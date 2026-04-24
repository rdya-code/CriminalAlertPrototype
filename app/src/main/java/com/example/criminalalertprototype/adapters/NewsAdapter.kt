package com.example.criminalalertprototype.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.api.Article

class NewsAdapter(
    private var newsList: List<Article>
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsList[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = newsList.size

    fun updateData(newList: List<Article>) {
        newsList = newList
        notifyDataSetChanged()
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_news_title)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_news_description)
        private val tvSource: TextView = itemView.findViewById(R.id.tv_news_source)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_news_time)

        fun bind(article: Article) {
            tvTitle.text = article.title
            tvDescription.text = article.description ?: "No description available"
            tvSource.text = article.source.name
            tvTime.text = formatDate(article.publishedAt)
        }

        private fun formatDate(dateString: String): String {
            return try {
                // Simple formatting - just show the date part
                dateString.substring(0, 10)
            } catch (e: Exception) {
                "Recent"
            }
        }
    }
}