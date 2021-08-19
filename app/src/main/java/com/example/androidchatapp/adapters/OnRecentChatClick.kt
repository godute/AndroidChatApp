package com.example.androidchatapp.adapters

import com.example.androidchatapp.models.RecentChatRoom

interface OnRecentChatClick {
    fun onChatClick(recentMessage: RecentChatRoom)
}