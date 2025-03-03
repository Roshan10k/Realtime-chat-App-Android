package com.example.connect_androidchatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connect_androidchatapp.R
import com.example.connect_androidchatapp.model.UserModel
import com.squareup.picasso.Picasso

class UserAdapter(
    private val userList: MutableList<UserModel>,
    private val lastMessages: Map<String, String>,
    private val onUserClick: (UserModel) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.chatImage)
        val username: TextView = itemView.findViewById(R.id.userName)
        val lastMessage: TextView = itemView.findViewById(R.id.lastMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.username.text = user.name
        holder.lastMessage.text = lastMessages[user.userID] ?: "No messages yet"

        // Load profile image using Picasso
        Picasso.get()
            .load(user.imageUrl) // Assuming UserModel has profileImageUrl
            .placeholder(R.drawable.baseline_person_24) // Default image
            .error(R.drawable.baseline_person_24) // Error image
            .into(holder.profileImage)

        holder.itemView.setOnClickListener {
            onUserClick(user) // Trigger onUserClick when a user item is clicked
        }
    }

    override fun getItemCount(): Int = userList.size
}
