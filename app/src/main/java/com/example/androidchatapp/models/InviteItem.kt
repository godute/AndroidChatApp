package com.example.androidchatapp.models

import android.view.View
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.InviteItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class InviteItem(private val inviteInfo: InviteInfo) : BindableItem<InviteItemBinding>() {
    override fun bind(viewBinding: InviteItemBinding, position: Int) {
        viewBinding.item = inviteInfo

    }

    override fun getLayout(): Int = R.layout.invite_item

    override fun initializeViewBinding(view: View): InviteItemBinding {
        return InviteItemBinding.bind(view)
    }
}