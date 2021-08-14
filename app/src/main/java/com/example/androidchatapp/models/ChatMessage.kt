package com.example.androidchatapp.models

data class ChatMessageList(
    val chatMessages: ArrayList<ChatMessage> = ArrayList()
)

data class ChatMessage(
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Long = 0
)