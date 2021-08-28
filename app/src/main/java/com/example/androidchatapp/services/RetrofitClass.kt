package com.example.androidchatapp.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClass {
    var api: FirebaseAPI
    private val BASE_URL = "https://fcm.googleapis.com"

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(FirebaseAPI::class.java)
    }
}