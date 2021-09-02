package com.example.androidchatapp.models

import android.view.View
import com.bumptech.glide.Glide
import com.example.androidchatapp.GlobalApplication
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.RowSendMessageBinding
import com.example.androidchatapp.services.FileDownloadInterface
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.viewbinding.BindableItem

class SendMessageItem(private val chatMessage: ChatMessage, private val listener: FileDownloadInterface) : BindableItem<RowSendMessageBinding>() {
    override fun bind(viewBinding: RowSendMessageBinding, position: Int) {
        viewBinding.message = chatMessage
        when(chatMessage.type) {
            MessageType.TEXT -> {
                viewBinding.chatToTextMessage.visibility = View.VISIBLE
            }
            MessageType.IMAGE -> {
                viewBinding.chatToImageMessage.visibility = View.VISIBLE

                FirebaseStorage.getInstance().getReference(chatMessage.content)
                    .downloadUrl
                    .addOnSuccessListener {
                        Glide.with(GlobalApplication.getContext())
                            .load(it)
                            .into(viewBinding.chatToImageMessage)
                    }
            }
            MessageType.FILE -> {
                viewBinding.chatToFileView.visibility = View.VISIBLE
                viewBinding.chatToFileView.setOnClickListener {
                    listener?.onFileUriClick(chatMessage.content)
                }
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