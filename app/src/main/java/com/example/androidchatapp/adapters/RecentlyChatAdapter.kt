package com.example.androidchatapp.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidchatapp.databinding.RecentlyChatItemBinding
import com.example.androidchatapp.models.ChatMessage


class RecentlyChatAdapter(val chatMessages: List<ChatMessage>) : RecyclerView.Adapter<RecentlyChatAdapter.RecentlyChatViewHolder>() {
    class RecentlyChatViewHolder(private val binding: RecentlyChatItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyChatViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecentlyChatViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}