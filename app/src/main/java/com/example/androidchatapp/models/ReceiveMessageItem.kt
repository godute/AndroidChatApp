package com.example.androidchatapp.models

import android.view.View
import com.bumptech.glide.Glide
import com.example.androidchatapp.GlobalApplication
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.RowReceiveMessageBinding
import com.example.androidchatapp.services.FileDownloadInterface
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.viewbinding.BindableItem

class ReceiveMessageItem(private val chatMessage: ChatMessage, private val listener: FileDownloadInterface) : BindableItem<RowReceiveMessageBinding>() {
    override fun bind(viewBinding: RowReceiveMessageBinding, position: Int) {
        viewBinding.message = chatMessage
        when(chatMessage.type) {
            MessageType.TEXT -> {
                viewBinding.chatFromTextMessage.visibility = View.VISIBLE
            }
            MessageType.IMAGE -> {
                viewBinding.chatFromImageMessage.visibility = View.VISIBLE

                FirebaseStorage.getInstance().getReference(chatMessage.content)
                    .downloadUrl
                    .addOnSuccessListener {
                        Glide.with(GlobalApplication.getContext())
                            .load(it)
                            .into(viewBinding.chatFromImageMessage)
                    }
            }
            MessageType.FILE -> {
                viewBinding.chatFromFileViewLayout.visibility = View.VISIBLE
                viewBinding.chatFromFileName.text = chatMessage.content.substring(chatMessage.content.lastIndexOf("/")+1)
                viewBinding.chatFromFileView.setOnClickListener {
                    listener.onFileUriClick(chatMessage.content)
                }
            }
        }
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