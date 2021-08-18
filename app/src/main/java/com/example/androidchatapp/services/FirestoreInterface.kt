package com.example.androidchatapp.services

import com.example.androidchatapp.models.ChatMessage
import com.example.androidchatapp.models.UserInfo

interface FirestoreGetUsersListener {
    fun onGetAllUserComplete()
    fun onGetUserComplete(userInfo: UserInfo)
}

interface FirestoreGetRoomListener {
    fun onGetRoomComplete()
    fun onGetMessage(message: ChatMessage)
}