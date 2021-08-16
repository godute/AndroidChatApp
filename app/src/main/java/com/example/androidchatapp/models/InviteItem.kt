package com.example.androidchatapp.models

import android.view.View
import com.example.androidchatapp.R
import com.example.androidchatapp.adapters.OnInviteCheck
import com.example.androidchatapp.databinding.InviteItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class InviteItem(private val inviteInfo: InviteInfo, private val onInviteCheck: OnInviteCheck) : BindableItem<InviteItemBinding>() {
    override fun bind(viewBinding: InviteItemBinding, position: Int) {
        viewBinding.item = inviteInfo
        viewBinding.inviteCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                onInviteCheck.onCheck(inviteInfo.userInfo.userId)
            } else {
                onInviteCheck.onUncheck(inviteInfo.userInfo.userId)
            }
        }
    }

    override fun getLayout(): Int = R.layout.invite_item

    override fun initializeViewBinding(view: View): InviteItemBinding {
        return InviteItemBinding.bind(view)
    }
}