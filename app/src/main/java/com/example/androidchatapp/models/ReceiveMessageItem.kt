package com.example.androidchatapp.models

import android.view.View
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.RowReceiveMessageBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.viewbinding.BindableItem

class ReceiveMessageItem(private val chatMessage: ChatMessage) : BindableItem<RowReceiveMessageBinding>() {
    override fun bind(viewBinding: RowReceiveMessageBinding, position: Int) {
        viewBinding.message = chatMessage
        FirebaseFirestore.getInstance().collection("users")
            .document(chatMessage.senderId)
            .get()
            .addOnSuccessListener {
                if(it != null) {
                    viewBinding.chatFromUserName.text = it.data?.get("name").toString()
                }
            }
    }

    override fun getLayout(): Int = R.layout.row_receive_message

    override fun initializeViewBinding(view: View): RowReceiveMessageBinding {
        return RowReceiveMessageBinding.bind(view)
    }

}