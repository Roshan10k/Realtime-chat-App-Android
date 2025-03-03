package com.example.connect_androidchatapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.connect_androidchatapp.R
import com.example.connect_androidchatapp.databinding.ActivityChangePasswordBinding
import com.example.connect_androidchatapp.repository.UserRepositoryImpl
import com.example.connect_androidchatapp.ui.fragment.ProfileFragment
import com.example.connect_androidchatapp.viewModel.UserViewModel

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePasswordBinding
    lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        binding.backChange.setOnClickListener {
           finish()
        }

        binding.resetButton.setOnClickListener {
            val email = binding.textInputForgotEmail.text.toString()

            // Validate input
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.forgetPassword(email) { success, message ->

                if (success) {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    finish() // Close the activity after success
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}