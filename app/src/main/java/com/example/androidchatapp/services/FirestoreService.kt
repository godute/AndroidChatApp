package com.example.androidchatapp.services

import android.util.Log
import com.example.androidchatapp.models.SharedViewModel
import com.example.androidchatapp.models.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

private const val TAG = "FirestoreService"

object FirestoreService {
    private var _currentUser = UserInfo()
    val CurrentUser: UserInfo get() = _currentUser

    private lateinit var _fireStoreListener: FirestoreGetUsersListener

    private var _refUser = FirebaseFirestore.getInstance().collection("users")
    private var _refRoom = FirebaseFirestore.getInstance().collection("rooms")

    fun setOnFireStoreListener(listener: FirestoreGetUsersListener) {
        _fireStoreListener = listener
    }

    fun fetchUsers() {
        SharedViewModel.initGroup()

        _refUser.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                Log.d(TAG, "Current data: ${snapshot.metadata}")
                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d(TAG, "New User: ${dc.document.data}")
                            val userInfo = dc.document.toObject(UserInfo::class.java)

                            val groupName: String =
                                if (userInfo.userId == Firebase.auth.currentUser!!.uid) {
                                    SharedViewModel.setCurrentUser(userInfo)
                                    "내 프로필"
                                } else {
                                    "친구목록"
                                }
                            SharedViewModel.addUser(groupName, userInfo)
                        }
                        DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified: ${dc.document.data}")
                        DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed: ${dc.document.data}")
                    }
                }
            } else {
                Log.d(TAG, "Current data: null")
            }
            _fireStoreListener.onGetAllUserComplete()
        }
    }

    fun getUser(uid: String) {
        Log.d(TAG, "onProfileClick Called $uid")

        _refUser
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                Log.d(TAG, "Success get User ${document.data?.get("userId").toString()}")

                val userInfo = document.toObject(UserInfo::class.java)

                if (userInfo != null) {
                    _fireStoreListener.onGetUserComplete(userInfo)
                }
            }
    }

    fun stopListener() {
    }
}