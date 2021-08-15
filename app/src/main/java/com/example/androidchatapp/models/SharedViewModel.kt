package com.example.androidchatapp.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val TAG = "SharedViewModel"

object SharedViewModel {
    private var _currentUser = UserInfo()
    val CurrentUser:UserInfo = _currentUser

    private val _groupList = MutableLiveData<HashMap<String, ArrayList<UserInfo>>>()
    val GroupList: LiveData<HashMap<String, ArrayList<UserInfo>>> = _groupList

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
}