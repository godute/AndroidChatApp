package com.example.androidchatapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidchatapp.ChatActivity
import com.example.androidchatapp.TabActivity
import com.example.androidchatapp.adapters.OnRecentChatClick
import com.example.androidchatapp.databinding.FragmentRecentlyChatBinding
import com.example.androidchatapp.models.RecentChatRoom
import com.example.androidchatapp.models.RecentMessageItem
import com.example.androidchatapp.models.SharedViewModel
import com.example.androidchatapp.services.FirestoreRecentChatRoomListener
import com.example.androidchatapp.services.FirestoreService
import com.xwray.groupie.GroupieAdapter

private const val TAG = "RecentlyChatFragment"

class RecentlyChatFragment : Fragment(), FirestoreRecentChatRoomListener, OnRecentChatClick {
    companion object{
        val RECENT_MESSAGE = "RECENT_MESSAGE"
    }
    private var _binding: FragmentRecentlyChatBinding? = null
    private val binding get() = _binding!!

    private val groupieAdapter = GroupieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecentlyChatBinding.inflate(inflater, container, false)

        binding.apply {
            recentlyChatFragment = this@RecentlyChatFragment
            lifecycleOwner = viewLifecycleOwner
        }
        setupRecyclerView()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        FirestoreService.setTabActivity(activity as TabActivity)
        FirestoreService.setOnFireStoreRecentChatRoomListener(this)
        fetchRecentChatList()
    }

    private fun fetchRecentChatList() {
        SharedViewModel.initRecentMessage()

        FirestoreService.fetchRecentMessages()
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView Called")
        binding.recentlyChatRecyclerView.apply {
            setHasFixedSize(true)
        }
    }

    override fun onRecentChatModified() {
        Log.d(TAG, "onRecentChatModified Called")
        val sortedChatMap = SharedViewModel.RecentChatList.value!!.toList()
            .sortedByDescending { (_, value) -> value.timestamp }.toMap()

        groupieAdapter.clear()
        for ((_, v) in sortedChatMap) {
            groupieAdapter.add(RecentMessageItem(v, this))
        }

        binding.recentlyChatRecyclerView.adapter = groupieAdapter
    }

    override fun onChatClick(recentMessage: RecentChatRoom) {
        Log.d(TAG, "onChatClick Called")
        activity?.let {
            val intent = Intent(it, ChatActivity::class.java)
            intent.putExtra(RECENT_MESSAGE, recentMessage)
            it.startActivity(intent)
        }
    }
}