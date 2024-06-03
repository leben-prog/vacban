// UsersAdapter.kt
package com.example.store

import User
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vacban.DbHelper
import com.example.vacban.R

class UsersAdapter(private val users: List<User>, private val dbHelper: DbHelper) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val loginTextView: TextView = view.findViewById(R.id.user_login)
        val nameTextView: TextView = view.findViewById(R.id.user_name)
        val passTextView: TextView = view.findViewById(R.id.user_pass)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_req, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.loginTextView.text = user.login
        holder.nameTextView.text = user.name
        holder.passTextView.text = user.pass
    }

    override fun getItemCount() = users.size
}