package com.example.connect_androidchatapp.repository

import android.util.Log
import com.example.connect_androidchatapp.model.MessageModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class MessageRepositoryImpl : MessageRepository {
    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.reference.child("Messages")

    override fun sendMessage(message: MessageModel, callback: (Boolean, String?) -> Unit) {
        // Create a unique chat ID that's the same regardless of who started the chat
        val chatId = if (message.senderId < message.receiverId) {
            "${message.senderId}_${message.receiverId}"
        } else {
            "${message.receiverId}_${message.senderId}"
        }

        // Generate a unique key for this message
        val messageId = messagesRef.child(chatId).push().key ?: return

        // Save the message under this key
        messagesRef.child(chatId).child(messageId).setValue(message)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                Log.e("MessageRepo", "Failed to send message: ${e.message}")
                callback(false, e.message)
            }
    }

    override fun getMessages(senderId: String, receiverId: String, callback: (List<MessageModel>) -> Unit) {
        // Get chat ID (consistent order regardless of who started the chat)
        val chatId = if (senderId < receiverId) {
            "${senderId}_${receiverId}"
        } else {
            "${receiverId}_${senderId}"
        }

        messagesRef.child(chatId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = ArrayList<MessageModel>()

                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(MessageModel::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }

                // Sort messages by timestamp
                messages.sortBy { it.timestamp }

                callback(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MessageRepo", "Error fetching messages: ${error.message}")
                callback(emptyList())
            }
        })
    }

    // Add method to delete a message
    fun deleteMessage(messageId: String, chatId: String, callback: (Boolean, String?) -> Unit) {
        messagesRef.child(chatId).child(messageId).removeValue()
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    // Add method to mark messages as read
    fun markAsRead(chatId: String, messageId: String, callback: (Boolean) -> Unit) {
        messagesRef.child(chatId).child(messageId).child("read").setValue(true)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}