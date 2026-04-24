package com.example.criminalalertprototype.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val id: Int = 0,
    val username: String,
    val notificationMode: String = "all",
    val stealthMode: Boolean = false,
    val lastActive: String = ""
) : Parcelable