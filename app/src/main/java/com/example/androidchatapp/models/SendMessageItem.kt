package com.example.androidchatapp.models

import android.view.View
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.RowSendMessageBinding
import com.xwray.groupie.viewbinding.BindableItem

class SendMessageItem(private val message: Message) : BindableItem<RowSendMessageBinding>() {
    override fun bind(viewBinding: RowSendMessageBinding, position: Int) {
        viewBinding.message = message
    }

    override fun getLayout(): Int = R.layout.row_send_message
    override fun initializeViewBinding(view: View): RowSendMessageBinding {
        return RowSendMessageBinding.bind(view)
    }


}