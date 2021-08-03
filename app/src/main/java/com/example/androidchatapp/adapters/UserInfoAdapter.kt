package com.example.androidchatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidchatapp.databinding.UserItemBinding
import com.example.androidchatapp.models.UserInfo

class UserInfoAdapter(private val userList: List<UserInfo>) :
    RecyclerView.Adapter<UserInfoAdapter.UserInfoViewHolder>() {
    class UserInfoViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserInfo) {
            with(binding) {
                userName.text = user.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoViewHolder {
        return UserInfoViewHolder(
            UserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: UserInfoViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount() = userList.size

}
