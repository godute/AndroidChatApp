package com.example.androidchatapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.databinding.ActivityChatBinding
import com.example.androidchatapp.models.Message
import com.example.androidchatapp.models.ReceiveMessageItem
import com.example.androidchatapp.models.SendMessageItem
import com.google.firebase.Timestamp
import com.xwray.groupie.GroupieAdapter

private const val TAG = "ChatActivity"
class ChatActivity : AppCompatActivity() {
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityChatBinding.inflate(layoutInflater)
        binding.apply {
            chatActivity = this@ChatActivity
            chatRecyclerView.apply {
                setHasFixedSize(true)

                val groupieAdapter = GroupieAdapter()

                groupieAdapter.add(SendMessageItem(Message("1", "2", "1", "hi wootae", Timestamp.now())))

                groupieAdapter.add(ReceiveMessageItem(Message("1", "2", "1", "hi ~", Timestamp.now())))

                layoutManager = LinearLayoutManager(context)

                adapter = groupieAdapter

            }
        }
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
    }

    fun onBackButtonClick() {
        Log.d(TAG, "onBackButtonClick() called")
    }
}
