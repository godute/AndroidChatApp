package com.example.androidchatapp.models

import com.google.firebase.Timestamp

data class MessageList(
    val chatPartner: String,
    val messages: List<Message>
)

data class Message(
    val senderId: String,
    val receiverId: String,
    val msgId: String,
    val text: String,
    val timestamp: Timestamp

)