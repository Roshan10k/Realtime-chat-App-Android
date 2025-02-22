package com.example.connect_androidchatapp.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.connect_androidchatapp.R
import com.example.connect_androidchatapp.databinding.ActivitySignupBinding
import com.example.connect_androidchatapp.model.UserModel
import com.example.connect_androidchatapp.repository.UserRepositoryImpl
import com.example.connect_androidchatapp.utils.ImageUtils
import com.example.connect_androidchatapp.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var imageUtils: ImageUtils
    private var imageUri: Uri? = null
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUtils = ImageUtils(this).apply {
            registerActivity { uri ->
                uri?.let {
                    imageUri = it
                    Picasso.get().load(it).into(binding.profilePic)
                }
            }
        }

        binding.profilePic.setOnClickListener { imageUtils.launchGallery(this) }

        userViewModel = UserViewModel(UserRepositoryImpl())

        binding.backToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.signupButton.setOnClickListener {
            val email = binding.textInputEditEmail.text.toString().trim()
            val name = binding.textInputEditName.text.toString().trim()
            val password = binding.textInputEditPassword.text.toString().trim()

            if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri == null) {
                Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createFirebaseUser(email, password, name)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createFirebaseUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result.user?.uid ?: run {
                        Toast.makeText(this, "User creation failed", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                    uploadImage(userId, email, name)
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadImage(userId: String, email: String, name: String) {
        imageUri?.let { uri ->
            userViewModel.uploadImage(this, uri) { imageUrl ->
                if (imageUrl != null) {
                    saveUserToDatabase(userId, email, name, imageUrl)
                } else {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUserToDatabase(userId: String, email: String, name: String, imageUrl: String) {
        val user = UserModel(
            userID = userId,
            email = email,
            name = name,
            imageUrl = imageUrl
        )

        userViewModel.addUserToDatabase(userId, user) { isSuccess, message ->
            if (isSuccess) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Database error: $message", Toast.LENGTH_SHORT).show()
                // Optional: Delete Firebase user if database save fails
                auth.currentUser?.delete()
            }
        }
    }
}