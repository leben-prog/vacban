package com.example.vacban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * AdminActivity displays all travel requests in a RecyclerView.
 * Uses RequestAdapter to handle delete and status-edit actions.
 */
class AdminActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RequestAdapter
    private val requestList = mutableListOf<AdminRequest>()
    private var filterUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Retrieve optional user filter from intent
        filterUid = intent.getStringExtra("USER_UID")

        recyclerView = findViewById(R.id.rv_requests)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RequestAdapter(
            requestList,
            onDeleteClick = { request ->
                db.collection("requests").document(request.uid).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Заявка удалена", Toast.LENGTH_SHORT).show()
                        loadRequests()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Ошибка удаления: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            },
            onEditStatusClick = { request ->
                val statuses = arrayOf("Ожидание", "Одобрено", "Отказано")
                AlertDialog.Builder(this)
                    .setTitle("Выберите статус")
                    .setItems(statuses) { _, which ->
                        val newStatus = statuses[which]
                        db.collection("requests").document(request.uid)
                            .update("status", newStatus)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Статус обновлён", Toast.LENGTH_SHORT).show()
                                loadRequests()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Ошибка обновления: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    .show()
            }
        )
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btn_manage_destinations).setOnClickListener {
            startActivity(Intent(this, DestinationsActivity::class.java))
        }

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loadRequests()
    }

    private fun loadRequests() {
        if (filterUid != null) {
            // Load single user's request
            db.collection("requests").document(filterUid!!).get()
                .addOnSuccessListener { doc ->
                    requestList.clear()
                    if (doc.exists()) {
                        val uid = doc.id
                        val email = doc.getString("email") ?: ""
                        val departureDate = doc.getString("departureDate") ?: ""
                        val arrivalDate = doc.getString("arrivalDate") ?: ""
                        val tripType = doc.getString("tripType") ?: ""
                        val tripDest = doc.getString("tripDest") ?: ""
                        val status = doc.getString("status") ?: ""
                        requestList.add(
                            AdminRequest(
                                uid = uid,
                                email = email,
                                tripType = tripType,
                                tripDest = tripDest,
                                departureDate = departureDate,
                                arrivalDate = arrivalDate,
                                status = status
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка загрузки заявок: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            db.collection("requests").get()
                .addOnSuccessListener { result ->
                    requestList.clear()
                    for (doc in result) {
                        val uid = doc.id
                        val email = doc.getString("email") ?: ""
                        val departureDate = doc.getString("departureDate") ?: ""
                        val arrivalDate = doc.getString("arrivalDate") ?: ""
                        val tripType = doc.getString("tripType") ?: ""
                        val tripDest = doc.getString("tripDest") ?: ""
                        val status = doc.getString("status") ?: ""
                        requestList.add(
                            AdminRequest(
                                uid = uid,
                                email = email,
                                tripType = tripType,
                                tripDest = tripDest,
                                departureDate = departureDate,
                                arrivalDate = arrivalDate,
                                status = status
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка загрузки заявок: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
} 