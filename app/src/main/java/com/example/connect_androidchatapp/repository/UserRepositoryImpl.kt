package com.example.connect_androidchatapp.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.connect_androidchatapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.io.InputStream
import java.util.concurrent.Executors

class UserRepositoryImpl:UserRepository {
        var auth: FirebaseAuth = FirebaseAuth.getInstance()
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference = database.reference.child("Users")

        override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    callback(true,"Login Successful")
                }else{
                    callback(false,it.exception?.message.toString())
                }
            }

        }

        override fun register(
            email: String,
            password: String,
            callback: (Boolean, String, String) -> Unit
        ) {
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    callback(true,"Registration success",
                        auth.currentUser?.uid.toString())
                }else{
                    callback(false,it.exception?.message.toString(),"")
                }
            }
        }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Password reset link sent to $email")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }


    override fun addUserToDatabase(
            userID: String,
            userModel: UserModel,
            callback: (Boolean, String) -> Unit
        ) {
            reference.child(userID.toString()).setValue(userModel).addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Registration success")
                } else {
                    callback(false, it.exception?.message.toString())
                }
            }
        }

        override fun logout(callback: (Boolean, String) -> Unit) {
            try {
                auth.signOut() // Perform sign out
                callback(true, "Logout successful") // Success message
                } catch (e: Exception) {
                callback(false, "Logout failed: ${e.message}") // Error message
            }
        }

    override fun editProfile(
        userId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        Log.d("EditProfile", "Updating profile for user: $userId with data: $data") // Debug log

        reference.child(userId).updateChildren(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("EditProfile", "Update task completed: success")
                    callback(true, "Profile updated successfully")
                } else {
                    Log.e("EditProfile", "Update failed", task.exception)
                    callback(false, task.exception?.message ?: "Failed to update profile")
                }
            }
    }



    override fun getCurrentUSer(): FirebaseUser? {

            return auth.currentUser
        }

    override fun getUserFromDatabase(userId: String, callback: (UserModel?, Boolean, String) -> Unit) {
        reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    callback(userModel, true, "User fetched")
                } else {
                    callback(null, false, "User not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dkqxtiohw",
            "api_key" to "134567786659159",
            "api_secret" to "Pet6ruRRN4vU13n49MGHSOGOVq4"
        )
    )

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)

                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    override fun saveUserFCMToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            auth.currentUser?.uid?.let { uid ->
                reference.child(uid).child("fcmToken").setValue(token)
            }
        }
    }
}


