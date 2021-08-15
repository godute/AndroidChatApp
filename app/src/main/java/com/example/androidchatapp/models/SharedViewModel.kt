package com.example.androidchatapp.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val TAG = "SharedViewModel"

object SharedViewModel {
    private var _currentUser = UserInfo()
    val CurrentUser:UserInfo get() = _currentUser

    private val _groupList = MutableLiveData<HashMap<String, ArrayList<UserInfo>>>()
    val GroupList: LiveData<HashMap<String, ArrayList<UserInfo>>> = _groupList

    private val _recentChatList = MutableLiveData<HashMap<String, RecentChatRoom>>()
    val RecentChatList: LiveData<HashMap<String, RecentChatRoom>> = _recentChatList

    fun initGroup() {
        _groupList.value = HashMap()

        _groupList.value!!["내 프로필"] = arrayListOf()

        _groupList.value!!["친구목록"] = arrayListOf()
    }

    // 현재 로그인한 사용자 설정
    fun setCurrentUser(currentUser: UserInfo) {
        _currentUser = currentUser
    }

    fun addUser(group: String, user: UserInfo) {
        _groupList.value?.get(group)?.add(user)
        Log.d(TAG, "group List: ${_groupList.value}")
    }

    fun initRecentMessage() {
        _recentChatList.value = HashMap()
    }

    fun setRecentMessage(key: String, message: RecentChatRoom) {
        _recentChatList.value?.put(key, message)
    }
}