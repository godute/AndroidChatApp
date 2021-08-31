package com.example.androidchatapp.services

import android.util.Log
import com.example.androidchatapp.ChatActivity
import com.example.androidchatapp.GlobalApplication
import com.example.androidchatapp.TabActivity
import com.example.androidchatapp.models.*
import com.example.androidchatapp.utils.getJsonDataFromSecrets
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val TAG = "FirestoreService"

object FirestoreService {
    private var _currentUser = UserInfo()
    val CurrentUser: UserInfo get() = _currentUser
    private lateinit var retrofit:Retrofit
    private var firebaseAPI = RetrofitClass.api

    private lateinit var _fireStoreGetAllUserListener: FirestoreGetAllUserListener
    private lateinit var _fireStoreGetUserListener: FirestoreGetUserListener
    private var _fireStoreRoomListener: FirestoreGetRoomListener? = null
    private var _fireStoreRecentChatRoomListener: FirestoreRecentChatRoomListener? = null

    private var _refUser = FirebaseFirestore.getInstance().collection("users")
    private var _refRoom = FirebaseFirestore.getInstance().collection("rooms")
    private var _refToken = FirebaseFirestore.getInstance().collection("tokens")

    private lateinit var _groupId: String

    private lateinit var _chatActivity: ChatActivity
    private lateinit var _tabActivity: TabActivity

    fun setOnFireStoreGetAllUserListener(listener: FirestoreGetAllUserListener) {
        _fireStoreGetAllUserListener = listener
    }

    fun setOnFireStoreGetUserListner(listener: FirestoreGetUserListener) {
        _fireStoreGetUserListener = listener
    }

    fun setOnFireStoreRoomListener(listener: FirestoreGetRoomListener) {
        _fireStoreRoomListener = listener
    }

    fun setOnFireStoreRecentChatRoomListener(listener: FirestoreRecentChatRoomListener) {
        _fireStoreRecentChatRoomListener = listener
    }

    fun setChatActivity(activity: ChatActivity) {
        _chatActivity = activity
    }

    fun setTabActivity(activity: TabActivity) {
        _tabActivity = activity
    }

    fun closeFireStoreRoomListener() {
        _fireStoreRoomListener = null
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
                        DocumentChange.Type.ADDED ,DocumentChange.Type.MODIFIED -> {
                            Log.d(TAG, "New User: ${dc.document.data}")
                            val userInfo = dc.document.toObject(UserInfo::class.java)

                            val groupName: String =
                                if (userInfo.userId == Firebase.auth.currentUser!!.uid) {
                                    SharedViewModel.setCurrentUser(userInfo)
                                    "내 프로필"
                                } else {
                                    "동료 목록"
                                }
                            SharedViewModel.addUser(groupName, userInfo)
                        }

                        DocumentChange.Type.REMOVED -> {
                            Log.d(TAG, "Removed: ${dc.document.data}")
                        }
                    }
                }
            } else {
                Log.d(TAG, "Current data: null")
            }
            _fireStoreGetAllUserListener.onGetAllUserComplete()
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
                    _fireStoreGetUserListener.onGetUserComplete(userInfo)
                }
            }
    }

    private fun listenRoom() {
//        initRetrofit()
        _refRoom.addSnapshotListener(_chatActivity) { snapshot, error ->
            if (error != null) {
                Log.d(TAG, "addSnapshotListener failed.")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            Log.d(TAG, "ADDED: ${dc.document.data}")
                            val userList = dc.document.data.get("userList") as ArrayList<String>
                            userList.map { userId ->
                                _refUser.document(userId).get().addOnSuccessListener { docu ->
                                    val roomList =
                                        docu.data?.get("roomList") as? HashMap<String, String> ?: HashMap()
                                    val groupList =
                                        docu.data?.get("groupList") as? HashMap<String, ArrayList<String>> ?: HashMap()

                                    groupList?.set(_groupId, userList)

                                    roomList?.set(_groupId, dc.document.id)
                                    _refUser.document(userId).update("groupList", groupList)
                                    _refUser.document(userId).update("roomList", roomList)
                                }
                            }

                            _fireStoreRoomListener?.onGetRoomComplete(userList)
                        }

                        DocumentChange.Type.REMOVED -> {
                            Log.d(TAG, "Removed: ${dc.document.data}")
                        }
                    }
                }
            } else {
                Log.d(TAG, "Current data: null")
            }

        }
    }

    private fun listenMessage(roomId: String) {
        _refRoom.document(roomId).collection("messages").orderBy("timestamp")
            .addSnapshotListener(_chatActivity) { snapshot, e ->
                if (e != null) {
                    Log.d(TAG, "Listen Failed", e)
                    return@addSnapshotListener
                }
                var lastMessage = ChatMessage()
                for (dc in snapshot!!.documentChanges) {
                    Log.d(TAG, "${dc.document.data}")
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val message = dc.document.toObject(ChatMessage::class.java)
                            lastMessage = message

                            _fireStoreRoomListener?.onGetMessage(message)
                        }
                    }

                }

                _refRoom.document(roomId).get().addOnSuccessListener {
                    val recentMessage = RecentChatMessage("", lastMessage.senderId, lastMessage.content)
                    _refRoom.document(roomId).update("recentMessage", recentMessage)
                    _refRoom.document(roomId).update("timestamp", lastMessage.timestamp)
                }

            }
    }

    private fun sendPushMessage(receiverList: ArrayList<String>, sender: String, message: String) {
        receiverList.map { userId ->
            _refToken.document(userId).get().addOnSuccessListener {
                Log.d(TAG, "token : ${it.data?.get("token")}")
                val notificationItem = NotificationItem("ChatApp", "$sender \n $message")
                val fcmItem = FCMItem(it.data?.get("token").toString(), "high", notificationItem)
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "key=${getJsonDataFromSecrets(GlobalApplication.getContext()).toString()}"

                firebaseAPI.sendFCM(headers, fcmItem).enqueue(object : Callback<ResponseDTO> {
                    override fun onResponse(
                        call: Call<ResponseDTO>,
                        response: Response<ResponseDTO>
                    ) {
                        Log.d(TAG, "Push Message Success")
                    }

                    override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                        Log.d(TAG, "Push Message Failed")
                    }
                })
            }
        }
    }

    fun addUserToRoom(groupId: String, roomId: String, userList: ArrayList<String>) {
        Log.d(TAG, "addUserToRoom ($groupId, $roomId, $userList) Called")
        _groupId = groupId

        _refRoom.document(roomId).get().addOnSuccessListener { document ->
            val originUserList = document.data?.get("userList") as ArrayList<String>

            userList.addAll(originUserList)

            _refRoom.document(roomId).update("userList", userList)
        }
    }

    fun createRoom(groupId: String, roomId: String, userList: ArrayList<String>) {
        Log.d(TAG, "CreateRoom(GroupId: $groupId , RoomId : $roomId , UserList : $userList) Called")
        _groupId = groupId

        _refRoom.document(roomId)
            .set(
                hashMapOf(
                    "groupId" to groupId,
                    "roomId" to roomId,
                    "userList" to userList
                )
            )
            .addOnSuccessListener {
                Log.d(TAG, "Create Room $roomId Success")
                getRoom(groupId, roomId)
            }

    }

    fun getRoom(groupId: String, roomId: String) {
        _groupId = groupId

        listenRoom()

        listenMessage(roomId)
    }

    fun sendMessage(roomId: String, message: ChatMessage) {
        Log.d(TAG, "sendMessage() called")

        val msgRef = _refRoom.document(roomId).collection("messages")
            .document()
        msgRef.set(message)

        _refUser.document(message.senderId).get().addOnSuccessListener { sendUser ->
            val senderName = sendUser.data?.get("name").toString()

            _refRoom.document(roomId).get()
                .addOnSuccessListener { roomUsers ->
                    val userList = roomUsers.data?.get("userList") as? ArrayList<String> ?: ArrayList()
                    userList.remove(message.senderId)
                    sendPushMessage(userList, senderName, message.content)
                }
        }


    }

    fun fetchRecentMessages() {
        _refRoom.whereArrayContains("userList", Firebase.auth.currentUser!!.uid)
            .addSnapshotListener { snapshot, e->
            if(e != null) {
                Log.d(TAG, "fetchRecentMessage addSnapshot Listener failed")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                Log.d(TAG, "Current data: ${snapshot.metadata}")
                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED,
                        DocumentChange.Type.MODIFIED -> {
                            Log.d(TAG, "Added or Modified: ${dc.document.data}")
                            val chatRoom = dc.document.toObject(RecentChatRoom::class.java)
                            SharedViewModel.setRecentMessage(dc.document.id, chatRoom!!)
                        }
                        DocumentChange.Type.REMOVED -> {
                            Log.d(TAG, "Removed: ${dc.document.data}")
                            SharedViewModel.removeRecentMessage(dc.document.id)
                        }
                    }
                    _fireStoreRecentChatRoomListener?.onRecentChatModified()
                }
            } else {
                Log.d(TAG, "Current data: null")
            }

        }
    }
}