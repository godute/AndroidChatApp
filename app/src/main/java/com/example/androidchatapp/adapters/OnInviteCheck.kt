package com.example.androidchatapp.adapters

interface OnInviteCheck {
    fun onCheck(userId: String)
    fun onUncheck(userId: String)
}