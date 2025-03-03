package com.example.connect_androidchatapp.api


import com.example.connect_androidchatapp.model.NotificationModel



import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface NotificationInterface {
    @POST("/v1/projects/connect-chat-app-c64d3/messages:send")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    fun sendNotification(
        @Body message: NotificationModel,
        @Header("Authorization") accessToken: String
    ): retrofit2.Call<NotificationModel>
}