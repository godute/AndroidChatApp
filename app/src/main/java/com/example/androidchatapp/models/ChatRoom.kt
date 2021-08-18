package com.example.androidchatapp.models

data class ChatRoom(
    val roomId: String = "",
    val userList: ArrayList<String> = ArrayList(),
    val chatMessageList: ArrayList<ChatMessage> = arrayListOf()
)