package com.example.androidchatapp.models

import android.view.View
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.RowReceiveMessageBinding
import com.xwray.groupie.viewbinding.BindableItem

class ReceiveMessageItem(private val message: Message) : BindableItem<RowReceiveMessageBinding>() {
    override fun bind(viewBinding: RowReceiveMessageBinding, position: Int) {
        viewBinding.message = message
    }

    override fun getLayout(): Int = R.layout.row_receive_message

    override fun initializeViewBinding(view: View): RowReceiveMessageBinding {
        return RowReceiveMessageBinding.bind(view)
    }

}