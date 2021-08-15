package com.example.androidchatapp.models

import android.view.View
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.RecentlyChatItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class RecentMessageItem(private val recentMessage: RecentChatMessage) : BindableItem<RecentlyChatItemBinding>() {
    override fun bind(viewBinding: RecentlyChatItemBinding, position: Int) {
        viewBinding.message = recentMessage
    }

    override fun getLayout(): Int = R.layout.recently_chat_item

    override fun initializeViewBinding(view: View): RecentlyChatItemBinding {
        return RecentlyChatItemBinding.bind(view)
    }

}