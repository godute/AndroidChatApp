package com.example.androidchatapp.services

import android.app.NotificationManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.androidchatapp.R
import com.example.androidchatapp.utils.sendNotification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val TAG = "FCMService"
class FCMService : FirebaseMessagingService() {
    private val TOPIC = "ChatApp"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if(remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Receive ${remoteMessage.data}")
        }

        if(remoteMessage.notification != null) {
            val body = remoteMessage.notification!!.body
            val title = remoteMessage.notification!!.title
            Log.d(TAG, "Message Notification Title: $title")
            Log.d(TAG, "Message Notification body: $body")

            showNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken $token")

        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        Log.d(TAG, "here! sendRegistrationToServer! token is $token")
        FirebaseFirestore.getInstance().collection("token")
            .document()
            .set(mapOf("token" to token))
    }

    private fun showNotification(title: String?, body: String?) {
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)
        if (body != null) {
            notificationManager?.sendNotification(body, applicationContext)
        }
    }

    private fun subscribeTopic() {
        // [START subscribe_topic]
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            .addOnCompleteListener { task ->
                var message = applicationContext.getString(R.string.message_subscribed)
                if (!task.isSuccessful) {
                    message = applicationContext.getString(R.string.message_subscribe_failed)
                }
                Toast.makeText(applicationContext , message, Toast.LENGTH_SHORT).show()
            }
        // [END subscribe_topics]
    }
}