package com.example.androidchatapp.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

private const val TAG = "SharedViewModel"

object SharedViewModel {

    private var _currentUser = UserInfo()
    val CurrentUser:UserInfo get() = _currentUser

    private var _groupList = MutableLiveData<HashMap<String, ArrayList<UserInfo>>>()
    val GroupList: LiveData<HashMap<String, ArrayList<UserInfo>>> = _groupList

    private val _recentChatList = MutableLiveData<HashMap<String, RecentChatRoom>>()
    val RecentChatList: LiveData<HashMap<String, RecentChatRoom>> = _recentChatList

    fun initGroup() {
        _groupList.value = HashMap()

        _groupList!!.value?.set("내 프로필", arrayListOf())

        _groupList!!.value?.set("동료 목록", arrayListOf())
    }

    // 현재 로그인한 사용자 설정
    fun setCurrentUser(currentUser: UserInfo) {
        _currentUser = currentUser
    }

    fun addUser(group: String, user: UserInfo) {
        val userInfo = _groupList?.value?.get(group)?.singleOrNull { u -> u.userId == user.userId }

        if(userInfo == null) {
            _groupList?.value?.get(group)?.add(user)
        }
        else {
            val index = _groupList?.value?.get(group)?.indexOf(userInfo)
            if (index != null) {
                _groupList?.value?.get(group)?.set(index, user)
            }
        }

        Log.d(TAG, "group List: ${_groupList}")
    }

    fun initRecentMessage() {
        _recentChatList.value = HashMap()
    }

    fun setRecentMessage(key: String, message: RecentChatRoom) {
        _recentChatList.value?.put(key, message)
    }

    fun removeRecentMessage(key: String) {
        _recentChatList.value?.remove(key)
    }
}