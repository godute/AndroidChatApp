package com.example.androidchatapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.adapters.RecentlyChatAdapter
import com.example.androidchatapp.databinding.FragmentRecentlyChatBinding
import com.example.androidchatapp.models.MessageList

private const val TAG = "RecentlyChatFragment"

class RecentlyChatFragment : Fragment() {
    private var _binding: FragmentRecentlyChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var messageList: List<MessageList>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecentlyChatBinding.inflate(inflater,container, false)

        binding.apply {
            recentlyChatFragment = this@RecentlyChatFragment
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView Called")
        binding.recentlyChatRecyclerView.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(requireActivity())

            adapter = RecentlyChatAdapter(listOf())
//            adapter = GroupInfoAdapter(userList?.value as List<GroupInfo>, this@UserListFragment)

        }
    }
}