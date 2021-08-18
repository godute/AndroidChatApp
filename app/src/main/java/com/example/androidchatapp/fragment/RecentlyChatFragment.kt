package com.example.androidchatapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.TabActivity
import com.example.androidchatapp.databinding.FragmentRecentlyChatBinding
import com.example.androidchatapp.models.RecentMessageItem
import com.example.androidchatapp.models.SharedViewModel
import com.example.androidchatapp.services.FirestoreRecentChatRoomListener
import com.example.androidchatapp.services.FirestoreService
import com.xwray.groupie.GroupieAdapter

private const val TAG = "RecentlyChatFragment"

class RecentlyChatFragment : Fragment(), FirestoreRecentChatRoomListener {
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

//    private fun fetchRecentChatList() {
//        SharedViewModel.initRecentMessage()
//
//        FirebaseFirestore.getInstance().collection("users")
//            .document(CurrentUser.userId)
//            .get()
//            .addOnSuccessListener { document ->
//                val roomList = document.data?.get("roomList") as HashMap<*, *>
//
//                for ((_, roomId) in roomList) {
//                        FirebaseFirestore.getInstance().collection("rooms").document(roomId as String)
//                            .addSnapshotListener { snapshot, e ->
//                                if (e != null) {
//                                    Log.d(TAG, "Listen failed", e)
//                                    return@addSnapshotListener
//                                }
//
//                                if(snapshot != null) {
//                                    val recentRoom = snapshot.toObject(RecentChatRoom::class.java)
//                                    SharedViewModel.setRecentMessage(snapshot!!.id, recentRoom!!)
//                                }
//
//                                val sortedChatMap = SharedViewModel.RecentChatList.value!!.toList()
//                                    .sortedBy { (_, value) -> value.timestamp }.toMap()
//
//                                groupieAdapter.clear()
//                                for ((_, v) in sortedChatMap) {
//                                    groupieAdapter.add(RecentMessageItem(v.recentMessage))
//                                }
//
//                                binding.recentlyChatRecyclerView.adapter = groupieAdapter
//                            }
//                }
//            }
//    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView Called")
        binding.recentlyChatRecyclerView.apply {
            setHasFixedSize(true)

            if(requireActivity() != null) {
                layoutManager = LinearLayoutManager(requireActivity())
            }
        }
    }

    override fun onRecentChatModified() {
        Log.d(TAG, "onRecentChatModified Called")
        val sortedChatMap = SharedViewModel.RecentChatList.value!!.toList()
            .sortedBy { (_, value) -> value.timestamp }.toMap()

        groupieAdapter.clear()
        for ((_, v) in sortedChatMap) {
            groupieAdapter.add(RecentMessageItem(v.recentMessage))
        }

        binding.recentlyChatRecyclerView.adapter = groupieAdapter
    }
}