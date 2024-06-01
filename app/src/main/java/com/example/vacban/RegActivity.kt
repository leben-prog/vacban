package com.example.vacban

import DbHelper
import MainActivity
import User
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class RegActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        val userName: EditText = findViewById(R.id.nameEditText)
        val userLogin: EditText = findViewById(R.id.loginEditText)
        val userPassword: EditText = findViewById(R.id.passwordEditText)
        val registerButton: Button = findViewById(R.id.registerButton)
        val cancelButton: Button = findViewById(R.id.cancelButton)

        registerButton.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val email = userName.text.toString().trim()
            val pass = userPassword.text.toString().trim()

            if (login.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            } else {
                val user = User(login, email, pass)

                val db = DbHelper(this) // Создание нового объекта на основе класса DbHelper
                db.addUser(user)
                Toast.makeText(this, "Пользователь $login добавлен", Toast.LENGTH_LONG).show()
                userLogin.text.clear()
                userName.text.clear()
                userPassword.text.clear()
            }
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
