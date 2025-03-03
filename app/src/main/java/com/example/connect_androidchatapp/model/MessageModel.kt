package com.example.connect_androidchatapp.model

data class MessageModel(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0,
    var messageId: String = ""
) {

}