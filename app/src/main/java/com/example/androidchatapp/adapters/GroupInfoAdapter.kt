package com.example.androidchatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidchatapp.databinding.GroupItemBinding
import com.example.androidchatapp.models.GroupInfo

class GroupInfoAdapter(private val userList: List<GroupInfo>, private val mCallback: OnItemClick) :
    RecyclerView.Adapter<GroupInfoAdapter.GroupInfoViewHolder>() {
    class GroupInfoViewHolder(
        private val binding: GroupItemBinding,
        private val mCallback: OnItemClick
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(groupInfo: GroupInfo) {
            binding.groupName.text = groupInfo.category
            binding.userListRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = UserInfoAdapter(groupInfo.userList, mCallback)
                addItemDecoration(
                    DividerItemDecoration(
                        binding.root.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupInfoViewHolder {
        return GroupInfoViewHolder(
            GroupItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ),
            mCallback
        )
    }

    override fun onBindViewHolder(holder: GroupInfoViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount() = userList.size
}
