package com.example.androidchatapp.services

import com.example.androidchatapp.models.ChatMessage
import com.example.androidchatapp.models.UserInfo

interface FirestoreGetAllUserListener {
    fun onGetAllUserComplete()
}

interface FirestoreGetUserListener {
    fun onGetUserComplete(userInfo: UserInfo)
}

interface FirestoreGetRoomListener {
    fun onGetRoomComplete()
    fun onGetMessage(message: ChatMessage)
}

interface FirestoreRecentChatRoomListener {
    fun onRecentChatModified()
}