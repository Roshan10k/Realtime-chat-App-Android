package com.example.connect_androidchatapp.model

data class NotificationData(
    val token: String = "",
    val notification: HashMap<String, String>? = null,
    val data: HashMap<String, String>? = null
)