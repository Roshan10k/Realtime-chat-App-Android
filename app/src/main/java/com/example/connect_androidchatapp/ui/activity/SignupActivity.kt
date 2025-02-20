package com.example.connect_androidchatapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.connect_androidchatapp.R
import com.example.connect_androidchatapp.databinding.ActivityLoginBinding
import com.example.connect_androidchatapp.databinding.ActivitySignupBinding
import com.example.connect_androidchatapp.model.UserModel
import com.example.connect_androidchatapp.repository.UserRepositoryImpl
import com.example.connect_androidchatapp.viewModel.UserViewModel

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = UserRepositoryImpl()
        userViewModel =UserViewModel(repo)

        binding.backToLogin.setOnClickListener {
            val intent = Intent(this@SignupActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signupButton.setOnClickListener {

            var email = binding.textInputEditEmail.text.toString()
            var password = binding.textInputEditPassword.text.toString()
            var name = binding.textInputEditName.text.toString()

            userViewModel.register(email, password) { success, message, userId ->
                if (success) {
                    var userModel = UserModel(userId.toString(),email,name,password)

                    addUser(userModel)
                    val intent = Intent(this@SignupActivity,LoginActivity::class.java)
                    finish()
                    startActivity(intent)

                } else {

                    Toast.makeText(
                        this@SignupActivity,message, Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private  fun addUser(userModel: UserModel){

        userViewModel.addUserToDatabase(userModel.userID,userModel){
                success, message ->
            if (success){

                Toast.makeText(this@SignupActivity,
                    message,Toast.LENGTH_LONG).show()

            }else{
                Toast.makeText(this@SignupActivity,
                    message,Toast.LENGTH_LONG).show()
            }



        }

    }
}