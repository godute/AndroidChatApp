package com.example.androidchatapp.services

import com.example.androidchatapp.models.UserInfo

interface FirestoreGetUsersListener {
    fun onGetAllUserComplete()
    fun onGetUserComplete(userInfo: UserInfo)
}