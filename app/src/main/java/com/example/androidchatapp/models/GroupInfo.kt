package com.example.androidchatapp.models

// user 목록에서 group
data class GroupInfo(
    val category: String,
    val userList: List<UserInfo>
)