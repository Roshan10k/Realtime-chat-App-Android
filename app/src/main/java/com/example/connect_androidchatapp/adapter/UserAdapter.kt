package com.example.connect_androidchatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connect_androidchatapp.R
import com.example.connect_androidchatapp.model.UserModel

class UserAdapter(
    private val users: List<UserModel>,
    private val lastMessages: Map<String, String>,
    private val onUserClick: (UserModel) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.userName)
        val lastMessage: TextView = itemView.findViewById(R.id.lastMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.username.text = user.name
        holder.lastMessage.text = lastMessages[user.userID] ?: "No messages yet"

        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }

    override fun getItemCount(): Int = users.size
}
