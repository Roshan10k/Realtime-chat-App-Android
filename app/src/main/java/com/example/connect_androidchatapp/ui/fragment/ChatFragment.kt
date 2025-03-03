package com.example.connect_androidchatapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connect_androidchatapp.adapter.UserAdapter
import com.example.connect_androidchatapp.databinding.FragmentChatBinding
import com.example.connect_androidchatapp.model.MessageModel

import com.example.connect_androidchatapp.model.UserModel
import com.example.connect_androidchatapp.ui.activity.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userAdapter: UserAdapter
    private val usersList = mutableListOf<UserModel>()
    private val lastMessagesMap = mutableMapOf<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize userAdapter here
        userAdapter = UserAdapter(usersList, lastMessagesMap, ::navigateToChatPage)

        fetchUsers()
        fetchLastMessages()

        binding.recyclerViewChats.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
            setHasFixedSize(true)
        }
    }

    private fun fetchUsers() {
        val currentUserId = auth.currentUser?.uid ?: return
        database.reference.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserModel::class.java)
                    if (user != null && user.userID != currentUserId) {
                        usersList.add(user)
                    }
                }
                userAdapter.notifyDataSetChanged() // Notify the adapter to refresh the data
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchLastMessages() {
        val currentUserId = auth.currentUser?.uid ?: return
        database.reference.child("Messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the existing map to avoid stale data
                lastMessagesMap.clear()

                for (chatSnapshot in snapshot.children) {
                    val chatId = chatSnapshot.key ?: continue

                    // Check if this chat involves the current user
                    if (chatId.contains(currentUserId)) {
                        // Extract the other user's ID from the chat ID
                        val otherUserId = if (chatId.startsWith(currentUserId + "_")) {
                            chatId.substring(currentUserId.length + 1)
                        } else {
                            chatId.substring(0, chatId.length - currentUserId.length - 1)
                        }

                        // Get the last message by sorting by timestamp
                        var lastMessage: MessageModel? = null
                        for (messageSnapshot in chatSnapshot.children) {
                            val message = messageSnapshot.getValue(MessageModel::class.java)
                            if (message != null && (lastMessage == null || message.timestamp > lastMessage.timestamp)) {
                                lastMessage = message
                            }
                        }

                        // Store the last message text for this user
                        if (lastMessage != null) {
                            lastMessagesMap[otherUserId] = lastMessage.message
                        }
                    }
                }

                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getFCMToken(onTokenReceived: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result ?: ""
                onTokenReceived(token)  // Return the token using the callback
            } else {
                onTokenReceived("")  // Return an empty string if failed
            }
        }
    }

    private fun navigateToChatPage(selectedUser: UserModel) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra("userId", selectedUser.userID)
        intent.putExtra("userName", selectedUser.name)
        intent.putExtra("ImageUrl", selectedUser.imageUrl)
        intent.putExtra("fcmToken", selectedUser.fcmToken)  // Use receiver's token from their model
        startActivity(intent)
    }





}
