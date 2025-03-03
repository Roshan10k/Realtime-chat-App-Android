package com.example.connect_androidchatapp.repository

import com.example.connect_androidchatapp.model.MessageModel

interface MessageRepository {

    fun sendMessage(message: MessageModel, callback: (Boolean, String?) -> Unit)

    fun getMessages(senderId: String, receiverId: String, callback: (List<MessageModel>) -> Unit)
}