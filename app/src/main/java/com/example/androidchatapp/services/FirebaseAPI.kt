package com.example.androidchatapp.services

import com.example.androidchatapp.models.FCMItem
import com.example.androidchatapp.models.ResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface FirebaseAPI {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAylMkMsQ:APA91bFMTaenDe2YYzvS7Db5vzkcX7W3lvOrfOqv4N_LH2n4uX8eILqDTgxIjuIapsFcuJ2zJnH_FmgPT0GATeYMznPRHk8HU6ASumKIV3RFDG1t5DBrkCD56LzQfVhAJj6u1vzu345V"
    )
    @POST("/fcm/send")
    fun sendFCM(@Body body: FCMItem): Call<ResponseDTO>
}