package com.example.connect_androidchatapp.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("update-fcm-token") // Replace with your actual endpoint
    fun updateFcmToken(@Body token: String): Call<ResponseBody>
}
