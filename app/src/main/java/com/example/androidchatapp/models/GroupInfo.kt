package com.example.androidchatapp.models

// user 목록에서 group
data class GroupInfo(
    var category: String = "",
    var userList: List<UserInfo> = listOf()
)