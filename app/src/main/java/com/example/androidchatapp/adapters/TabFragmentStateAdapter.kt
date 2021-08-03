package com.example.androidchatapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.androidchatapp.fragment.ChatFragment
import com.example.androidchatapp.fragment.GroupWareFragment
import com.example.androidchatapp.fragment.MoreFragment
import com.example.androidchatapp.fragment.UserListFragment

private const val tapCount: Int = 4
class TabFragmentStateAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return tapCount
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> UserListFragment()
            1 -> ChatFragment()
            2 -> GroupWareFragment()
            else -> MoreFragment()
        }
    }

}