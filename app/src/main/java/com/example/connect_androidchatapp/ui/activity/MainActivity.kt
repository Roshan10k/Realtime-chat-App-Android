package com.example.connect_androidchatapp.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connect_androidchatapp.R
import com.example.connect_androidchatapp.adapter.UserAdapter
import com.example.connect_androidchatapp.databinding.ActivityMainBinding
import com.example.connect_androidchatapp.model.UserModel
import com.example.connect_androidchatapp.repository.UserRepositoryImpl
import com.example.connect_androidchatapp.ui.fragment.ChatFragment
import com.example.connect_androidchatapp.ui.fragment.ProfileFragment
import com.example.connect_androidchatapp.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        // ✅ Now it's safe to call ViewModel methods
        userViewModel.saveUserFCMToken()

        replaceFragment(ChatFragment())
        binding.bottomNavigation.setOnItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.navChat -> replaceFragment(ChatFragment())
                R.id.navProfile -> replaceFragment(ProfileFragment())
            }
            true
        }

        // Adjust layout to avoid system bar overlap
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrame, fragment)
        fragmentTransaction.commit()
    }
}
