package com.example.vacban

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.SearchView

class AdminActivity : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper
    private lateinit var userList: List<User>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        dbHelper = DbHelper(this)
        userList = dbHelper.getAllUsers()
        val userNames = userList.map { it.name }

        val listView: ListView = findViewById(R.id.user_list)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userNames)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val userId = userList[position].id
            val intent = Intent(this, UserDetailActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }
}
