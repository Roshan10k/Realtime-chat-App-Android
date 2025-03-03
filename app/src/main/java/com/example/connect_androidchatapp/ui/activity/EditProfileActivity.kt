package com.example.connect_androidchatapp.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.connect_androidchatapp.R
import com.example.connect_androidchatapp.databinding.ActivityEditProfileBinding
import com.example.connect_androidchatapp.repository.UserRepositoryImpl
import com.example.connect_androidchatapp.viewModel.UserViewModel
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        // Get user ID from intent
        userId = intent.getStringExtra("userId").toString()

        // Fetch user details from database
        userViewModel.getUserFromDatabase(userId)



        // Observe user data and populate fields
        userViewModel.userData.observe(this) { user ->
            user?.let {
                binding.textInputEditEmail.setText(it.email ?: "")
                binding.textInputEditName.setText(it.name ?: "")
                Picasso.get()
                    .load(it.imageUrl)
                    .placeholder(R.drawable.baseline_person_24)
                    .into(binding.updateProfilePic)
            }
        }

        binding.updateButton.setOnClickListener {
            updateUserProfile()
        }

        binding.backChange.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateUserProfile() {
        val email = binding.textInputEditEmail.text.toString()
        val name = binding.textInputEditName.text.toString()

        val updatedData = mutableMapOf<String, Any>()
        updatedData["email"] = email
        updatedData["name"] = name

        Log.d("EditProfileActivity", "Calling editProfile for userId: $userId with data: $updatedData") // Debug log

        // Update user details in Firebase Database
        userViewModel.editProfile(userId, updatedData) { success, message ->
            Log.d("EditProfileActivity", "Callback received: success=$success, message=$message") // Debug log
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            if (success) finish()
        }
    }

}
