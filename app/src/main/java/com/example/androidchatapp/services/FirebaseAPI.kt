package com.example.androidchatapp.services

import com.example.androidchatapp.models.FCMItem
import com.example.androidchatapp.models.ResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST


interface FirebaseAPI {
    @POST("/fcm/send")
    fun sendFCM(@HeaderMap headers: Map<String, String>, @Body body: FCMItem): Call<ResponseDTO>
}