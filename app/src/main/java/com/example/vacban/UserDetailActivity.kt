package com.example.vacban

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity

class UserDetailActivity : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper
    private lateinit var requestList: List<UserRequest>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        val userId = intent.getIntExtra("userId", -1)
        dbHelper = DbHelper(this)
        requestList = dbHelper.getReq(userId)
        val requestDescriptions = requestList.map { "${it.category} (${it.status})" }

        val listView: ListView = findViewById(R.id.request_list)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, requestDescriptions)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val requestId = requestList[position].id
            val intent = Intent(this, EditRequestActivity::class.java)
            intent.putExtra("requestId", requestId)
            startActivity(intent)
        }
    }
}
