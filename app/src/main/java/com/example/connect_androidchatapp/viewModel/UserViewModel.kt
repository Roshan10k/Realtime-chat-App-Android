package com.example.connect_androidchatapp.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.connect_androidchatapp.model.UserModel
import com.example.connect_androidchatapp.repository.UserRepository
import com.google.firebase.auth.FirebaseUser

class UserViewModel(val repo: UserRepository) {

    // LiveData to observe user data
    private val _userData = MutableLiveData<UserModel?>()
    val userData: MutableLiveData<UserModel?> get() = _userData

    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        repo.login(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.register(email, password, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgetPassword(email, callback)
    }

    fun addUserToDatabase(userID: String, userModel: UserModel, callback: (Boolean, String) -> Unit) {
        repo.addUserToDatabase(userID, userModel, callback)
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout(callback)
    }

    fun editProfile(userId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        repo.editProfile(userId,data,callback)
    }

    fun getCurrentUSer(): FirebaseUser? {
        return repo.getCurrentUSer()
    }

    // Fetch user from database and update LiveData
    fun getUserFromDatabase(userID: String) {
        repo.getUserFromDatabase(userID) { user, success, message ->
            if (success) {
                _userData.value = user
            }
        }
    }

    fun uploadImage(context: Context,imageUri: Uri,callback: (String?) -> Unit){
        repo.uploadImage(context,imageUri,callback)
    }

    fun saveUserFCMToken() {
        repo.saveUserFCMToken()
    }
}
