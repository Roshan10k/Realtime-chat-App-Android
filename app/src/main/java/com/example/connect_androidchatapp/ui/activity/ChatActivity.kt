package com.example.connect_androidchatapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connect_androidchatapp.model.NotificationData
import com.example.connect_androidchatapp.model.NotificationModel
import com.example.connect_androidchatapp.AccessToken
import com.example.connect_androidchatapp.R
import com.example.connect_androidchatapp.api.NotificationApi
import com.example.connect_androidchatapp.databinding.ActivityChatBinding
import com.example.connect_androidchatapp.model.MessageModel
import com.example.connect_androidchatapp.repository.MessageRepositoryImpl
import com.example.connect_androidchatapp.repository.UserRepositoryImpl
import com.example.connect_androidchatapp.viewModel.MessageViewModel
import com.example.connect_androidchatapp.viewModel.MessageViewModelFactory
import com.example.connect_androidchatapp.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var adapter: MessageAdapter
    private lateinit var receiverId: String
    private lateinit var receiverName: String
    private var userImage: String? = null
    private  var fcmToken:String?=null
    private lateinit var currentUserId: String
    private lateinit var userViewModel:UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        // Get data from intent
        receiverId = intent.getStringExtra("userId") ?: ""
        receiverName = intent.getStringExtra("userName") ?: "Chat"
        userImage = intent.getStringExtra("ImageUrl")
        fcmToken = intent.getStringExtra("fcmToken")

        // Set receiver name in toolbar
        binding.profileName.text = receiverName

        if (!userImage.isNullOrEmpty()) {
            Picasso.get()
                .load(userImage)
                .placeholder(R.drawable.baseline_person_24) // Add a placeholder image
                .into(binding.profileImage)
        }

        // Get current user ID
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Setup back button
        binding.backbtn.setOnClickListener {
            val intent = Intent(this@ChatActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Setup ViewModel
        val repository = MessageRepositoryImpl()
        val factory = MessageViewModelFactory(repository)
        messageViewModel = ViewModelProvider(this, factory)[MessageViewModel::class.java]

        // Setup RecyclerView
        setupRecyclerView()

        // Load messages
        messageViewModel.loadMessages(currentUserId, receiverId)

        // Observe messages
        messageViewModel.messages.observe(this) { messages ->
            adapter.updateMessages(messages)
            // Scroll to the bottom
            if (messages.isNotEmpty()) {
                binding.ChatrecyclerView.smoothScrollToPosition(messages.size - 1)
            }
        }

        // Setup send button
        binding.layoutSend.setOnClickListener {
            val messageText = binding.inputMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                binding.inputMessage.text.clear()
                sendNotification(fcmToken.toString())
            }



        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter(currentUserId, emptyList())
        binding.ChatrecyclerView.adapter = adapter
        binding.ChatrecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun sendMessage(messageText: String) {
        val timestamp = System.currentTimeMillis()
        val message = MessageModel(
            senderId = currentUserId,
            receiverId = receiverId,
            message = messageText,
            timestamp = timestamp
        )
        messageViewModel.sendMessage(message)
    }

    private fun sendNotification(receiverToken: String) {
        android.util.Log.d("Notification", "Preparing to send notification to: $receiverToken")

        // Skip if token is empty or null
        if (receiverToken.isEmpty() || receiverToken == "null") {
            android.util.Log.e("FCM", "Invalid receiver token")
            return
        }

        val userID = userViewModel.getCurrentUSer()?.uid ?: return

        // Add loading indicator
        val loadingToast = Toast.makeText(this@ChatActivity, "Sending notification...", Toast.LENGTH_SHORT)
        loadingToast.show()

        FirebaseDatabase.getInstance().getReference("Users").child(userID)
            .get()
            .addOnSuccessListener { snapshot ->
                val userName = snapshot.child("name").value?.toString() ?: "Users"
                val messageText = binding.inputMessage.text.toString().trim()
                val previewText = if (messageText.length > 30) "${messageText.substring(0, 27)}..." else messageText

                val notification = NotificationModel(
                    message = NotificationData(
                        token = receiverToken,
                        notification = hashMapOf(
                            "title" to userName,
                            "body" to previewText
                        ),
                        data = hashMapOf(
                            "title" to "Connect",
                            "body" to "$userName sent you a message",
                            "click_action" to "OPEN_CHAT_ACTIVITY",
                            "user_id" to userID,
                            "user_name" to userName
                        )
                    )
                )

                // Get token asynchronously
                AccessToken.getAccessTokenAsync { token ->
                    loadingToast.cancel()

                    if (token != null) {
                        val authHeader = "Bearer $token"

                        NotificationApi.create().sendNotification(notification, authHeader)
                            .enqueue(object : retrofit2.Callback<NotificationModel> {
                                override fun onResponse(
                                    call: Call<NotificationModel?>,
                                    response: Response<NotificationModel?>
                                ) {
                                    android.util.Log.d("FCM", "Response: ${response.code()} ${response.message()}")
                                    // Only show toast on error, not success (less intrusive)
                                    if (!response.isSuccessful) {
                                        Toast.makeText(
                                            this@ChatActivity,
                                            "Notification failed: ${response.code()}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<NotificationModel?>,
                                    t: Throwable
                                ) {
                                    android.util.Log.e("FCM", "Error: ${t.message}", t)
                                    Toast.makeText(
                                        this@ChatActivity,
                                        "Failed to send notification",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    } else {
                        Toast.makeText(
                            this@ChatActivity,
                            "Failed to get access token",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                loadingToast.cancel()
                android.util.Log.e("FCM", "Failed to get user data: ${e.message}", e)
                Toast.makeText(this@ChatActivity, "Failed to get user data", Toast.LENGTH_SHORT).show()
            }
    }
}