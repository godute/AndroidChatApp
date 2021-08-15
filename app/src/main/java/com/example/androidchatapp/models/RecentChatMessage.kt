package com.example.androidchatapp.models

data class RecentChatRoom(
    val userList: ArrayList<String> = ArrayList(),
    val recentMessage: RecentChatMessage = RecentChatMessage(),
    val timestamp: Long = 0
)

data class RecentChatMessage(
    val userName: String = "",
    val profileImg: String = "",
    val recentMessage: String = "",
)