package com.example.androidchatapp.models

import android.view.View
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.RowSendMessageBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.viewbinding.BindableItem

class SendMessageItem(private val chatMessage: ChatMessage) : BindableItem<RowSendMessageBinding>() {
    override fun bind(viewBinding: RowSendMessageBinding, position: Int) {
        viewBinding.message = chatMessage
        when(chatMessage.type) {
            MessageType.TEXT -> {
                viewBinding.chatToTextMessage.visibility = View.VISIBLE
                viewBinding.chatToImageMessage.visibility = View.GONE
            }
            MessageType.IMAGE -> {
                viewBinding.chatToTextMessage.visibility = View.GONE
                viewBinding.chatToImageMessage.visibility = View.VISIBLE
            }
        }
        FirebaseFirestore.getInstance().collection("users")
            .document(chatMessage.senderId)
            .get()
            .addOnSuccessListener {
                if(it != null) {
                    viewBinding.chatToUserName.text = it.data?.get("name").toString()
                }
            }
    }

    override fun getLayout(): Int = R.layout.row_send_message
    override fun initializeViewBinding(view: View): RowSendMessageBinding {
        return RowSendMessageBinding.bind(view)
    }


}