package com.example.connect_androidchatapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.connect_androidchatapp.model.MessageModel
import com.example.connect_androidchatapp.repository.MessageRepository

class MessageViewModel(private val chatRepository: MessageRepository) : ViewModel() {
    private val _messages = MutableLiveData<List<MessageModel>>()
    val messages: LiveData<List<MessageModel>> get() = _messages

    fun sendMessage(message: MessageModel) {
        chatRepository.sendMessage(message) { success, error ->
            if (!success) {
                // Handle error (e.g., show a toast)
            }
        }
    }

    fun loadMessages(senderId: String, receiverId: String) {
        chatRepository.getMessages(senderId, receiverId) { messages ->
            _messages.postValue(messages)
        }
    }

}