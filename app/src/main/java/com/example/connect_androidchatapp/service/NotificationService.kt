package com.example.connect_androidchatapp.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.connect_androidchatapp.R
import com.example.connect_androidchatapp.network.ApiService
import com.example.connect_androidchatapp.network.RetrofitClient
import com.example.connect_androidchatapp.ui.activity.ChatActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random

class NotificationService : FirebaseMessagingService() {

    private val channelID = "class-update"
    private val channelName = "class-updates"

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


        Log.d("FCM_RECEIVE", "Message received from: ${message.from}")
        Log.d("FCM_RECEIVE", "Message data: ${message.data}")
        Log.d("FCM_RECEIVE", "Message notification: ${message.notification}")

        val intent = Intent(this, ChatActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val builder = NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(
                IconCompat.createWithResource(
                    applicationContext,
                    R.drawable.ic_launcher_foreground
                )
            )
            .setColor(applicationContext.getColor(R.color.black))
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setBadgeIconType(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setOngoing(false)
            .setLights(
                ContextCompat.getColor(applicationContext, R.color.black),
                5000,
                5000
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            with(NotificationManagerCompat.from(applicationContext)) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                notify(Random().nextInt(3000), builder.build())
            }
        } else {
            NotificationManagerCompat.from(applicationContext)
                .notify(Random().nextInt(3000), builder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH,
        )
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.description = "Chat message notifications"

        notificationManager.createNotificationChannel(channel)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "New token generated: $token")
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.updateFcmToken(token)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("FCM_TOKEN", "Token successfully sent to server.")
                } else {
                    Log.e("FCM_TOKEN", "Failed to send token: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("FCM_TOKEN", "Error sending token: ${t.message}")
            }
        })
    }
}
