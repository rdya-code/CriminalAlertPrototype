package com.example.criminalalertprototype.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.models.AlertModel

class AlertAdapter(
    private var alertList: List<AlertModel>,
    private val onItemClick: (AlertModel) -> Unit
) : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    private var filteredList: List<AlertModel> = alertList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incident_card, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = filteredList[position]
        holder.bind(alert)
        holder.itemView.setOnClickListener { onItemClick(alert) }
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            alertList
        } else {
            alertList.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.type.contains(query, ignoreCase = true) ||
                it.location.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun updateData(newList: List<AlertModel>) {
        alertList = newList
        filteredList = newList
        notifyDataSetChanged()
    }

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconType: ImageView = itemView.findViewById(R.id.icon_type)
        private val lblTitle: TextView = itemView.findViewById(R.id.lbl_title)
        private val lblTime: TextView = itemView.findViewById(R.id.lbl_time)
        private val lblDesc: TextView = itemView.findViewById(R.id.lbl_desc)

        fun bind(alert: AlertModel) {
            lblTitle.text = alert.title
            lblDesc.text = alert.description
            lblTime.text = alert.timeAgo
            
            // Set icon based on type
            when (alert.type.lowercase()) {
                "theft" -> iconType.setImageResource(R.drawable.ic_theft_custom)
                "suspicious" -> iconType.setImageResource(R.drawable.ic_suspicious_custom)
                "fire" -> iconType.setImageResource(R.drawable.ic_fire_custom)
                else -> iconType.setImageResource(R.drawable.ic_alert_custom)
            }
            
            // Set urgency color
            when (alert.urgency) {
                "High" -> iconType.setColorFilter(itemView.context.getColor(R.color.alert_red))
                "Medium" -> iconType.setColorFilter(itemView.context.getColor(R.color.warning_orange))
                else -> iconType.setColorFilter(itemView.context.getColor(R.color.safety_blue))
            }
        }
    }
}