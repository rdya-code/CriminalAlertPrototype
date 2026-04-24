package com.example.criminalalertprototype.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportModel(
    val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val urgency: String,
    val location: String,
    val status: String = "active",
    val createdAt: String = "",
    val userName: String = ""
) : Parcelable