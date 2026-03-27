package com.example.criminalalertprototype

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class activity_history : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This line links the Kotlin file to your XML layout
        setContentView(R.layout.activity_history)
    }
}