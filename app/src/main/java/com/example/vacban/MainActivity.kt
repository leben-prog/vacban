package com.example.vacban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userLogin: EditText = findViewById(R.id.user_login)
        val userPassword: EditText = findViewById(R.id.user_password)
        val loginButton: Button = findViewById(R.id.button_log)
        val registerButton: Button = findViewById(R.id.button_reg)


        registerButton.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }



        loginButton.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val pass = userPassword.text.toString().trim()

            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            } else if (login == "admin" && pass == "admin") {
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
            } else {
                val db = DbHelper(this)
                val isAuth = db.getUser(login, pass)

                if (isAuth) {
                    val userId = db.getUserIdByLogin(login)
                    if (userId != -1) {
                        Toast.makeText(this, "Пользователь $login авторизован", Toast.LENGTH_LONG).show()
                        userLogin.text.clear()
                        userPassword.text.clear()
                        val intent = Intent(this, UserActivity::class.java)
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Ошибка: не удалось получить идентификатор пользователя", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
