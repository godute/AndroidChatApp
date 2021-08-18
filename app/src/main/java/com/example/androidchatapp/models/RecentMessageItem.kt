package com.example.androidchatapp.models

import android.util.Log
import android.view.View
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.RecentlyChatItemBinding
import com.xwray.groupie.viewbinding.BindableItem
import java.text.SimpleDateFormat

class RecentMessageItem(private val recentMessage: RecentChatMessage, private val userList: ArrayList<String>, private val timestamp: Long) : BindableItem<RecentlyChatItemBinding>(){

    override fun bind(viewBinding: RecentlyChatItemBinding, position: Int) {
        viewBinding.message = recentMessage

        var text = ""
        userList.map { user->
            val name = SharedViewModel.GroupList.value!!["동료 목록"]?.singleOrNull { s -> s.userId == user }?.name
            if(name != null) text += "$name "
        }
        viewBinding.recentlyChatUserName.text = text
        val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
        viewBinding.recentlyChatTimestamp.text = sdf.format(timestamp * 1000L)
        Log.d("RecentMessageItem", "${viewBinding.recentlyChatUserName.text}, ${userList}")
    }

    override fun getLayout(): Int = R.layout.recently_chat_item

    override fun initializeViewBinding(view: View): RecentlyChatItemBinding {
        return RecentlyChatItemBinding.bind(view)
    }
}