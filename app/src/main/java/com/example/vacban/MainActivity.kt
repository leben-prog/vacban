package com.example.vacban

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userLogin: EditText = findViewById(R.id.user_login)
        val userPassword: EditText = findViewById(R.id.user_password)
        val loginButton: Button = findViewById(R.id.button)
        val registerButton: Button = findViewById(R.id.button2)

        // Add your code here to handle button clicks or other logic
    }
}
