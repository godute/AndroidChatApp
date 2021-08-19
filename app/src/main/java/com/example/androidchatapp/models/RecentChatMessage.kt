package com.example.androidchatapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecentChatRoom(
    val roomId: String = "",
    val groupId: String = "",
    val userList: ArrayList<String> = ArrayList(),
    val recentMessage: RecentChatMessage = RecentChatMessage(),
    val timestamp: Long = 0
) : Parcelable

@Parcelize
data class RecentChatMessage(
    val profileImg: String = "",
    val recentMessage: String = "",
) : Parcelable