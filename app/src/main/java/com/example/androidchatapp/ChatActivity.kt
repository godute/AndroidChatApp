package com.example.androidchatapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.databinding.ActivityChatBinding
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

//                groupieAdapter.add(ChatFromItem(0))
//                groupieAdapter.add(ChatToItem(1))
//                groupieAdapter.add(ChatFromItem(2))
//                groupieAdapter.add(ChatToItem(3))
//                groupieAdapter.add(ChatFromItem(4))
//                groupieAdapter.add(ChatToItem(5))
//                groupieAdapter.add(ChatFromItem(6))

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
