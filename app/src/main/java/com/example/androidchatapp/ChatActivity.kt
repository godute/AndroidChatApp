package com.example.androidchatapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.databinding.ActivityChatBinding
import com.example.androidchatapp.models.Message
import com.example.androidchatapp.models.SendMessageItem
import com.example.androidchatapp.models.UserInfo
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupieAdapter
import java.util.*

private const val TAG = "ChatActivity"
class ChatActivity : AppCompatActivity() {
    private var user: UserInfo? = null
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!
    val groupieAdapter = GroupieAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityChatBinding.inflate(layoutInflater)
        binding.apply {
            chatActivity = this@ChatActivity
            chatRecyclerView.apply {
                setHasFixedSize(true)

                layoutManager = LinearLayoutManager(context)

                adapter = groupieAdapter

            }
        }
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        user = intent.getParcelableExtra<UserInfo>(ProfileActivity.USER_KEY)
    }

    fun onBackButtonClick() {
        Log.d(TAG, "onBackButtonClick() called")
    }

    fun performSendMessage() {
        Log.d(TAG, "performSendMessage() called")
        val senderId = Firebase.auth.currentUser?.uid
        val receiverId = user?.uid
        val message = Message(senderId!!, receiverId!!, UUID.randomUUID().toString(), binding.chatSendMessageText.text.toString(), Timestamp.now())
        val reference = FirebaseFirestore.getInstance().collection("/messages")
            .document().set(message)
            .addOnSuccessListener {
                Log.d(TAG, "Success Send Message!!")
            }

        binding.chatSendMessageText.text.clear()
        groupieAdapter.add(SendMessageItem(message))
        binding.chatRecyclerView.adapter = groupieAdapter
    }
}
