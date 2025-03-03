package com.example.connect_androidchatapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connect_androidchatapp.R
import com.example.connect_androidchatapp.databinding.FragmentProfileBinding
import com.example.connect_androidchatapp.repository.UserRepositoryImpl
import com.example.connect_androidchatapp.ui.activity.ChangePasswordActivity
import com.example.connect_androidchatapp.ui.activity.EditProfileActivity
import com.example.connect_androidchatapp.ui.activity.LoginActivity
import com.example.connect_androidchatapp.viewModel.UserViewModel
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        val currentUser = userViewModel.getCurrentUSer()

        currentUser?.let {
            val userId = it.uid
            val email = it.email
            userViewModel.getUserFromDatabase(userId)

            // Navigate to EditProfileActivity when edit button is clicked
            binding.profileArrowImage.setOnClickListener {
                val intent = Intent(requireContext(), EditProfileActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            }


            binding.forgotPasswordbutton.setOnClickListener{
                val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
                startActivity(intent)


            }

            binding.logoutBtn.setOnClickListener {
                userViewModel.logout { success, message ->
                    if (success) {
                        // Navigate to LoginActivity after successful logout
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        // Handle logout failure (optional: show a Toast message)
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        // Observe user data and update UI
        userViewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.nameSetting.text = user?.name ?: "No Name"
            binding.userEmail.text = user?.email ?: "No Email"

            Picasso.get()
                .load(user?.imageUrl)
                .placeholder(R.drawable.baseline_person_24)
                .into(binding.settingProfileImage)
        }
    }
}
