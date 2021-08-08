package com.example.androidchatapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.FragmentChatBinding
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

private const val TAG = "ChatFragment"

private const val PARAM_USERID = "userId"

class ChatFragment : Fragment() {
    private var param_userId: String? = null
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param_userId = it.getString(PARAM_USERID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            chatFragment = this@ChatFragment
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView Called")

        binding.chatRecyclerView.apply {
            setHasFixedSize(true)

            val groupieAdapter = GroupieAdapter()

            groupieAdapter.add(ChatFromItem(0))
            groupieAdapter.add(ChatToItem(1))
            groupieAdapter.add(ChatFromItem(2))
            groupieAdapter.add(ChatToItem(3))
            groupieAdapter.add(ChatFromItem(4))
            groupieAdapter.add(ChatToItem(5))
            groupieAdapter.add(ChatFromItem(6))

            layoutManager = LinearLayoutManager(requireActivity())

            adapter = groupieAdapter

//            adapter = GroupInfoAdapter(userList?.value as List<GroupInfo>, this@UserListFragment)

        }
    }
}

class ChatFromItem(id: Long) : Item<GroupieViewHolder>(id) {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }

}

class ChatToItem(id: Long) : Item<GroupieViewHolder>(id) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}