package com.example.androidchatapp.models

data class FCMItem(
    var to: String = "",
    var priority: String = "high",
    var notification: NotificationItem = NotificationItem()
)

data class NotificationItem(
    var title: String = "",
    var body: String = ""
)