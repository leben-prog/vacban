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
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import android.text.InputType
import com.google.android.material.button.MaterialButtonToggleGroup

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
    private var selectedCategory = "Отпуск"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Logout button
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        layoutRequests = findViewById(R.id.layout_requests)
        // Category toggle group listener
        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggle_group_category)
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_vacations -> onVacationsClicked(findViewById(R.id.btn_vacations))
                    R.id.btn_business_trips -> onBusinessTripsClicked(findViewById(R.id.btn_business_trips))
                }
            }
        }

        // Set default category selection
        toggleGroup.check(R.id.btn_vacations)

        findViewById<Button>(R.id.button_newreq).setOnClickListener {
            val intent = Intent(this, StepActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_NEW_REQUEST)
        }

        // Delete own request
        findViewById<Button>(R.id.btn_delete_request_user).setOnClickListener {
            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            db.collection("requests").document(uid).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Заявка удалена", Toast.LENGTH_SHORT).show()
                    layoutRequests.removeAllViews()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка удаления: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        // Load requests and set initial state
        selectedCategory = "Отпуск"
        loadRequests()
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
                    showRequestsByCategory(selectedCategory)
                } else {
                    Toast.makeText(this, "Заявок нет", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка при загрузке заявок: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun addRequestToView(request: Request) {
        // Inflate MaterialCardView item layout
        val itemView = layoutInflater.inflate(R.layout.item_request_user, layoutRequests, false)
        val tvCategory = itemView.findViewById<TextView>(R.id.tvUserCategory)
        val tvDates = itemView.findViewById<TextView>(R.id.tvUserDates)
        val tvStatus = itemView.findViewById<TextView>(R.id.tvUserStatus)
        tvCategory.text = request.category
        tvDates.text = "${request.departureDate} - ${request.arrivalDate}"
        tvStatus.text = request.status
        layoutRequests.addView(itemView)
    }

    private fun showRequestsByCategory(category: String) {
        layoutRequests.removeAllViews()
        val filteredRequests = requests.filter { it.category.startsWith(category) }
        filteredRequests.forEach { addRequestToView(it) }
    }

    fun onVacationsClicked(view: View) {
        findViewById<TextView>(R.id.tv_title).text = "Ваши отпуска"
        selectedCategory = "Отпуск"
        showRequestsByCategory(selectedCategory)
    }

    fun onBusinessTripsClicked(view: View) {
        findViewById<TextView>(R.id.tv_title).text = "Ваши командировки"
        selectedCategory = "Командировка"
        showRequestsByCategory(selectedCategory)
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
