package com.example.vacban

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditRequestActivity : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper
    private var requestId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_request)

        requestId = intent.getIntExtra("requestId", -1)
        dbHelper = DbHelper(this)

        val statusEditText: EditText = findViewById(R.id.status_edit_text)
        val saveButton: Button = findViewById(R.id.save_button)

        saveButton.setOnClickListener {
            val newStatus = statusEditText.text.toString().trim()
            if (newStatus.isNotEmpty()) {
                dbHelper.updateRequestStatus(requestId, newStatus)
                Toast.makeText(this, "Статус обновлен", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Поле статуса не должно быть пустым", Toast.LENGTH_LONG).show()
            }
        }
    }
}
