package com.example.androidchatapp.services

import com.example.androidchatapp.models.UserInfo

object FirestoreService {
    private var _currentUser = UserInfo()
    val CurrentUser: UserInfo get() = _currentUser

}