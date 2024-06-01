package com.example.vacban

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

data class Request(
    val id: Int,
    val category: String,
    val departureDate: String,
    val arrivalDate: String,
    val status: String
)

class UserActivity : AppCompatActivity() {

    private lateinit var layoutRequests: LinearLayout
    private val requests = mutableListOf<Request>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        layoutRequests = findViewById(R.id.layout_requests)

        findViewById<Button>(R.id.button_newreq).setOnClickListener {
            val intent = Intent(this, StepActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_NEW_REQUEST)
        }

        findViewById<Button>(R.id.btn_vacations).setOnClickListener {
            onVacationsClicked(it)
        }

        findViewById<Button>(R.id.btn_business_trips).setOnClickListener {
            onBusinessTripsClicked(it)
        }

        // Загрузка заявок
        loadRequests()
    }

    private fun loadRequests() {
        // Пример добавления заявок
        requests.add(Request(1, "Отпуск: Москва", "10.10.2005", "10.10.2006", "Одобрен"))
        requests.add(Request(2, "Командировка: Москва", "10.10.2005", "10.10.2006", "Ожидание"))
        requests.add(Request(3, "Отпуск: Москва", "10.10.2005", "10.10.2006", "Отказано"))

        // Изначально показываем отпуска
        showRequestsByCategory("Отпуск")
    }

    private fun addRequestToView(request: Request) {
        val requestLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 8, 8, 8)
            setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
        }

        val categoryTextView = TextView(this).apply {
            text = request.category
            textSize = 18f
            setTextColor(Color.BLACK)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val datesTextView = TextView(this).apply {
            text = "${request.departureDate} - ${request.arrivalDate}"
            textSize = 16f
            setTextColor(Color.BLACK)
        }

        val statusTextView = TextView(this).apply {
            text = request.status
            textSize = 16f
            setTextColor(when (request.status) {
                "Одобрен" -> Color.GREEN
                "Ожидание" -> Color.BLUE
                "Отказано" -> Color.RED
                else -> Color.BLACK
            })
        }

        requestLayout.addView(categoryTextView)
        requestLayout.addView(datesTextView)
        requestLayout.addView(statusTextView)

        layoutRequests.addView(requestLayout)
    }

    private fun showRequestsByCategory(category: String) {
        layoutRequests.removeAllViews()
        val filteredRequests = requests.filter { it.category.startsWith(category) }
        filteredRequests.forEach { addRequestToView(it) }
    }

    fun onVacationsClicked(view: View) {
        findViewById<TextView>(R.id.tv_title).text = "Ваши отпуска"
        showRequestsByCategory("Отпуск")
    }

    fun onBusinessTripsClicked(view: View) {
        findViewById<TextView>(R.id.tv_title).text = "Ваши командировки"
        showRequestsByCategory("Командировка")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_NEW_REQUEST && resultCode == RESULT_OK) {
            val category = data?.getStringExtra("category") ?: return
            val departureDate = data.getStringExtra("departureDate") ?: return
            val arrivalDate = data.getStringExtra("arrivalDate") ?: return

            val newRequest = Request(requests.size + 1, category, departureDate, arrivalDate, "Ожидание")
            requests.add(newRequest)
            showRequestsByCategory(if (category.startsWith("Отпуск")) "Отпуск" else "Командировка")
        }
    }

    companion object {
        const val REQUEST_CODE_NEW_REQUEST = 1
    }
}
