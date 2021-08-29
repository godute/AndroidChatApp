package com.example.androidchatapp.utils

import android.content.Context
import org.json.JSONObject
import java.io.FileNotFoundException

fun getJsonDataFromSecrets(context: Context) : String? {
    val jsonString: String
    val serverKey: String
    try {
        jsonString = context.assets.open("secrets.json")
            .bufferedReader()
            .use { it.readText() }
        val jObject = JSONObject(jsonString)
        serverKey = jObject.getString("SERVER_KEY")
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        return null
    }
    return serverKey
}