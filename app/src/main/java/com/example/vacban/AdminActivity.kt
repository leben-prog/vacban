package com.example.vacban

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var requestListView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val requestIds = mutableListOf<String>()
    private val requestSummaries = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Hide unused fields
        findViewById<android.view.View>(R.id.user_login_input).visibility = View.GONE
        findViewById<android.view.View>(R.id.user_name_input).visibility = View.GONE
        findViewById<android.view.View>(R.id.user_pass_input).visibility = View.GONE
        findViewById<android.view.View>(R.id.add_user_button).visibility = View.GONE
        // Setup ListView for requests
        findViewById<ListView>(R.id.user_list_view).apply {
            requestListView = this
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, requestSummaries)
        requestListView.adapter = adapter

        loadRequests()

        requestListView.setOnItemClickListener { _, _, position, _ ->
            val uid = requestIds[position]
            showRequestOptions(uid)
        }

        // Logout button
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loadRequests() {
        db.collection("requests").get()
            .addOnSuccessListener { result ->
                requestIds.clear()
                requestSummaries.clear()
                for (doc in result) {
                    val uid = doc.id
                    val email = doc.getString("email") ?: uid
                    val tripType = doc.getString("tripType") ?: ""
                    val tripDest = doc.getString("tripDest") ?: ""
                    val status = doc.getString("status") ?: ""
                    val summary = "$email: $tripType $tripDest — $status"
                    requestIds.add(uid)
                    requestSummaries.add(summary)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка загрузки заявок: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showRequestOptions(uid: String) {
        val options = arrayOf("Удалить заявку", "Изменить статус")
        AlertDialog.Builder(this)
            .setTitle("Запрос пользователя")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> deleteRequest(uid)
                    1 -> showStatusDialog(uid)
                }
            }
            .show()
    }

    private fun deleteRequest(uid: String) {
        db.collection("requests").document(uid).delete()
            .addOnSuccessListener {
                loadRequests()
                Toast.makeText(this, "Заявка удалена", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка удаления: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showStatusDialog(uid: String) {
        val statuses = arrayOf("Ожидание", "Одобрено", "Отказано")
        AlertDialog.Builder(this)
            .setTitle("Выберите статус")
            .setItems(statuses) { _, which ->
                updateRequestStatus(uid, statuses[which])
            }
            .show()
    }

    private fun updateRequestStatus(uid: String, newStatus: String) {
        db.collection("requests").document(uid)
            .update("status", newStatus)
            .addOnSuccessListener {
                loadRequests()
                Toast.makeText(this, "Статус обновлён", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка обновления: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
} 