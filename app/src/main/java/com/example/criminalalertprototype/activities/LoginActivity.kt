package com.example.criminalalertprototype.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.criminalalertprototype.R

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        usernameEditText = findViewById(R.id.et_username)
        passwordEditText = findViewById(R.id.et_password)
        loginButton = findViewById(R.id.btn_login)

        // Set click listener for login button
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Simple validation (any username/password works for demo)
            if (username.isNotEmpty() && password.isNotEmpty()) {
                // F1: Pass data via Intent Extras
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_NAME", username)
                intent.putExtra("LOGIN_TIME", System.currentTimeMillis())
                startActivity(intent)
                finish() // Close LoginActivity
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}