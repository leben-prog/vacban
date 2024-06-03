package com.example.vacban

import User
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper
    private lateinit var userListView: ListView
    private lateinit var addUserButton: Button
    private lateinit var userLoginInput: EditText
    private lateinit var userNameInput: EditText
    private lateinit var userPassInput: EditText
    private lateinit var userAdapter: ArrayAdapter<User>
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        dbHelper = DbHelper(this)

        userListView = findViewById(R.id.user_list_view)
        addUserButton = findViewById(R.id.add_user_button)
        userLoginInput = findViewById(R.id.user_login_input)
        userNameInput = findViewById(R.id.user_name_input)
        userPassInput = findViewById(R.id.user_pass_input)

        loadUsers()

        addUserButton.setOnClickListener {
            val login = userLoginInput.text.toString().trim()
            val name = userNameInput.text.toString().trim()
            val pass = userPassInput.text.toString().trim()

            if (login.isNotEmpty() && name.isNotEmpty() && pass.isNotEmpty()) {
                val newUser = User(login, name, pass)
                dbHelper.addUser(newUser)
                userList.add(newUser)
                userAdapter.notifyDataSetChanged()
                userLoginInput.text.clear()
                userNameInput.text.clear()
                userPassInput.text.clear()
            } else {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_LONG).show()
            }
        }

        userListView.setOnItemLongClickListener { _, _, position, _ ->
            val user = userList[position]
            dbHelper.deleteUser(user)
            userList.removeAt(position)
            userAdapter.notifyDataSetChanged()
            true
        }
    }

    private fun loadUsers() {
        userList.addAll(dbHelper.getAllUsers())
        userAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        userListView.adapter = userAdapter
    }
}
