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
import com.example.connect_androidchatapp.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    private lateinit var database: FirebaseDatabase
//    private lateinit var users: ArrayList<UserModel>
//    private lateinit var usersAdapter: UserAdapter
//    private var user: UserModel? = null


    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrame , fragment)
        fragmentTransaction.commit()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        database = FirebaseDatabase.getInstance()
//        users = ArrayList()
//        usersAdapter = UserAdapter(this, users)
//
//        // Set up RecyclerView properly
//        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
//        binding.userRecyclerView.adapter = usersAdapter
//
//        // Fetch current user data
//        val currentUserId = FirebaseAuth.getInstance().uid
//        database.reference.child("Users").child(currentUserId!!)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    user = snapshot.getValue(UserModel::class.java)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Failed to fetch user data",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })
//
//        // Fetch all users
//        database.reference.child("Users").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                users.clear()
//                for (snapshot1 in snapshot.children) {
//                    val userModel = snapshot1.getValue(UserModel::class.java)
//                    if (userModel != null && userModel.userID != currentUserId) {
//                        users.add(userModel)
//                    }
//                }
//                usersAdapter.notifyDataSetChanged()
//                Toast.makeText(
//                    this@MainActivity,
//                    "Users fetched: ${users.size}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@MainActivity, "Error fetching users", Toast.LENGTH_SHORT).show()
//            }
//        })

        replaceFragment(ChatFragment())
        binding.bottomNavigation.setOnItemSelectedListener{menu ->
            when(menu.itemId){
                R.id.navChat -> replaceFragment(ChatFragment())
                R.id.navProfile -> replaceFragment(ChatFragment())

                else -> {}
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

}

