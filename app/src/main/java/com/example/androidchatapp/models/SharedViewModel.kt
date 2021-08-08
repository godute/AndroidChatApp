package com.example.androidchatapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    private val _currentUser = MutableLiveData<UserInfo>()
    val CurrentUser: LiveData<UserInfo> = _currentUser

    private val _groupList = MutableLiveData<ArrayList<GroupInfo>>()
    val GroupList: LiveData<ArrayList<GroupInfo>> = _groupList

    // 현재 로그인한 사용자 설정
    fun setCurrentUser(currentUser: UserInfo) {
        _currentUser.value = currentUser
    }

    // 등록된 유저들 리스트 설정
    fun setUserList(groupList: ArrayList<GroupInfo>)  {
        _groupList.value = groupList
    }

    fun addUser(user: UserInfo) {
        _groupList.value?.add(GroupInfo("새 친구", listOf(user)))
    }
    fun clearGroupList() {
        _groupList.value?.clear()
    }

}