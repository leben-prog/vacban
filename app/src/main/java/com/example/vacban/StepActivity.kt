package com.example.vacban

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

import com.example.vacban.DbHelper
import com.example.vacban.UserRequest

class StepActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_1step)

        dbHelper = DbHelper(this)

        category = intent.getStringExtra("category") ?: "vacation"

        val continueButton = findViewById<Button>(R.id.continueButton)
        val departureDateEditText = findViewById<EditText>(R.id.departureDateEditText)
        val arrivalDateEditText = findViewById<EditText>(R.id.arrivalDateEditText)
        val tripTypeSpinner = findViewById<Spinner>(R.id.tripTypeSpinner)
        val tripDestSpinner = findViewById<Spinner>(R.id.tripDestSpinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.trip_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tripTypeSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.trip_destinations,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tripDestSpinner.adapter = adapter
        }

        continueButton.setOnClickListener {
            val departureDate = departureDateEditText.text.toString()
            val arrivalDate = arrivalDateEditText.text.toString()
            val tripType = tripTypeSpinner.selectedItem.toString()
            val tripDestination = tripDestSpinner.selectedItem.toString()

            if (departureDate.isEmpty() || arrivalDate.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите даты", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = getCurrentUserId()
            val request = UserRequest(0, "$tripType: $tripDestination", departureDate, arrivalDate, "Ожидание", userId)

            dbHelper.addReq(request, userId)
            Toast.makeText(this, "Заявка создана", Toast.LENGTH_SHORT).show()

            val intent = Intent()
            intent.putExtra("category", "$tripType: $tripDestination")
            intent.putExtra("departureDate", departureDate)
            intent.putExtra("arrivalDate", arrivalDate)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun getCurrentUserId(): Int {
        return 1 // Пример
    }
}
