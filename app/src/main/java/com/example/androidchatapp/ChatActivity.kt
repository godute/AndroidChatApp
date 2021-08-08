package com.example.androidchatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
    }

    override fun onStart() {
        super.onStart()

    }
}