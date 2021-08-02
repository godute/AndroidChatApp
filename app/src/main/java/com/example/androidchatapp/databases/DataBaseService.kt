package com.example.androidchatapp.databases

import com.google.firebase.database.FirebaseDatabase

class DataBaseService {
    private lateinit var _database: FirebaseDatabase

    fun init() {
        _database = FirebaseDatabase.getInstance()

    }
}