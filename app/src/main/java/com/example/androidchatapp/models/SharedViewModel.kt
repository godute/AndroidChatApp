package com.example.androidchatapp.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "SharedViewModel"

class SharedViewModel: ViewModel() {
    private val _currentUser = MutableLiveData<UserInfo>()
    val CurrentUser: LiveData<UserInfo> = _currentUser

    private val _groupList = MutableLiveData<HashMap<String, ArrayList<UserInfo>>>()
    val GroupList: LiveData<HashMap<String, ArrayList<UserInfo>>> = _groupList

    private val _userList = MutableLiveData<GroupInfo>()
    val UserList:LiveData<GroupInfo> = _userList

    fun initGroup() {
        _groupList.value = HashMap()

        _groupList.value!!["내 프로필"] = arrayListOf()

        _groupList.value!!["친구목록"] = arrayListOf()
    }

    // 현재 로그인한 사용자 설정
    fun setCurrentUser(currentUser: UserInfo) {
        _currentUser.value = currentUser
    }

    // 등록된 유저들 리스트 설정
    fun setUserList(groupList: ArrayList<GroupInfo>)  {
//        _groupList.value = groupList
    }

    fun addUser(group: String, user: UserInfo) {
//        _groupList.value?.add(GroupInfo("새 친구", listOf(user)))
        _groupList.value?.get(group)?.add(user)
        Log.d(TAG, "group List: ${_groupList.value}")
    }
    fun clearGroupList() {
        _groupList.value?.clear()
    }

}