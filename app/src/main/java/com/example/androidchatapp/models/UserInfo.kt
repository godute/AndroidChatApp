package com.example.androidchatapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    val userId: String = "",
    var name: String = "",
    var profileImg: String = "",
    var roomList: HashMap<String, String> = HashMap(),
    var employeeNumber: Int = 0
) : Parcelable
