package com.example.androidchatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidchatapp.databinding.UserItemBinding
import com.example.androidchatapp.models.UserInfo

class UserInfoAdapter(private val userList: List<UserInfo>, private val callback: OnItemClick) :
    RecyclerView.Adapter<UserInfoAdapter.UserInfoViewHolder>() {

    class UserInfoViewHolder(
        private val binding: UserItemBinding,
        private val callback: OnItemClick
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserInfo) {
            with(binding) {
                userName.text = user.name
                userProfile.setOnClickListener {
                    callback.onProfileClick(user.userId)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserInfoAdapter.UserInfoViewHolder {
        return UserInfoAdapter.UserInfoViewHolder(
            UserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ),
            callback
        )
    }

    override fun onBindViewHolder(holder: UserInfoAdapter.UserInfoViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount() = userList.size

}
