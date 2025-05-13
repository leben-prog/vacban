package com.example.vacban

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.view.View

data class Request(
    val id: Int,
    val category: String,
    val departureDate: String,
    val arrivalDate: String,
    val status: String
)

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var layoutRequests: LinearLayout
    private val requests = mutableListOf<Request>()

    private lateinit var btnVacations: Button
    private lateinit var btnBusinessTrips: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        layoutRequests = findViewById(R.id.layout_requests)
        btnVacations = findViewById(R.id.btn_vacations)
        btnBusinessTrips = findViewById(R.id.btn_business_trips)

        btnVacations.setOnClickListener {
            onVacationsClicked(it)
        }

        btnBusinessTrips.setOnClickListener {
            onBusinessTripsClicked(it)
        }

        findViewById<Button>(R.id.button_newreq).setOnClickListener {
            val intent = Intent(this, StepActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_NEW_REQUEST)
        }

        // Load requests and set initial state
        loadRequests()
        onVacationsClicked(btnVacations) // Make "Vacations" button active by default
    }

    private fun loadRequests() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_LONG).show()
            return
        }
        db.collection("requests").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val departureDate = document.getString("departureDate") ?: ""
                    val arrivalDate = document.getString("arrivalDate") ?: ""
                    val tripType = document.getString("tripType") ?: ""
                    val tripDest = document.getString("tripDest") ?: ""
                    val category = "$tripType: $tripDest"
                    requests.clear()
                    layoutRequests.removeAllViews()
                    val status = document.getString("status") ?: "Ожидание"
                    val request = Request(0, category, departureDate, arrivalDate, status)
                    requests.add(request)
                    addRequestToView(request)
                } else {
                    Toast.makeText(this, "Заявок нет", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка при загрузке заявок: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun addRequestToView(request: Request) {
        val requestLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
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
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 0, 0, 0) // Shift text right
            }
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        val datesTextView = TextView(this).apply {
            text = "${request.departureDate} - ${request.arrivalDate}"
            textSize = 16f
            setTextColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 0, 0, 0) // Shift text right
            }
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
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
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 0, 0, 0) // Shift text right
            }
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
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
        setButtonActive(btnVacations)
        setButtonInactive(btnBusinessTrips)
        showRequestsByCategory("Отпуск")
    }

    fun onBusinessTripsClicked(view: View) {
        findViewById<TextView>(R.id.tv_title).text = "Ваши командировки"
        setButtonActive(btnBusinessTrips)
        setButtonInactive(btnVacations)
        showRequestsByCategory("Командировка")
    }

    private fun setButtonActive(button: Button) {
        button.isSelected = true
        button.setBackgroundResource(if (button.id == R.id.btn_vacations) R.drawable.button_left_active else R.drawable.button_right_active)
        button.setTextColor(resources.getColor(R.color.white))
    }

    private fun setButtonInactive(button: Button) {
        button.isSelected = false
        button.setBackgroundResource(if (button.id == R.id.btn_vacations) R.drawable.button_left_inactive else R.drawable.button_right_inactive)
        button.setTextColor(resources.getColor(R.color.purple))
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
