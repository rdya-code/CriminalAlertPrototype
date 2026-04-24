package com.example.criminalalertprototype.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlertModel(
    val id: String,
    val title: String,
    val description: String,
    val type: String,        // "Theft", "Suspicious", "Fire", etc.
    val timeAgo: String,
    val urgency: String,     // "Low", "Medium", "High"
    val location: String,
    val isVerified: Boolean = false
) : Parcelable