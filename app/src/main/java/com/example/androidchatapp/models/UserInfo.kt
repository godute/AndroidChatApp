package com.example.androidchatapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    val uid: String,
    var name: String,
    var profileImg: String,
    var employeeNumber: Int
) : Parcelable
