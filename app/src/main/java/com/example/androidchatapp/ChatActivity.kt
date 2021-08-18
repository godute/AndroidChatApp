package com.example.androidchatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.InviteActivity.Companion.ROOM_KEY
import com.example.androidchatapp.InviteActivity.Companion.USERLIST_KEY
import com.example.androidchatapp.databinding.ActivityChatBinding
import com.example.androidchatapp.models.*
import com.example.androidchatapp.services.FirestoreGetRoomListener
import com.example.androidchatapp.services.FirestoreService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupieAdapter
import java.util.*

private const val TAG = "ChatActivity"

class ChatActivity : AppCompatActivity(), FirestoreGetRoomListener {
    companion object {
        val INVITED_USER_LIST = "INVITED_USER_LIST"
    }
    private var user: UserInfo? = null
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!
    private val groupieAdapter = GroupieAdapter()

    private var userListInRoom: ArrayList<String> = arrayListOf()

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result:ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Get result")
            val intent = result.data
            val invitedUserList = intent?.getStringArrayListExtra(INVITED_USER_LIST)
            Log.d(TAG, "Invited user : $invitedUserList")

            if(userListInRoom.count() == 2) {
                invitedUserList?.let { makeGroup(it) }
            }
        }
    }

    private lateinit var _roomId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityChatBinding.inflate(layoutInflater)
        binding.apply {
            chatActivity = this@ChatActivity
            chatRecyclerView.apply {
                setHasFixedSize(true)

                layoutManager = LinearLayoutManager(context)

                adapter = groupieAdapter
            }
        }
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        _roomId = ""

        fab_open = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)

        try {
            user = intent.getParcelableExtra<UserInfo>(ProfileActivity.USER_KEY)

            Log.d(TAG, user!!.name)

        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }

        FirestoreService.setChatActivity(this)
        FirestoreService.setOnFireStoreRoomListener(this)
        findRoom()

    }

    override fun onPause() {
        super.onPause()
        FirestoreService.closeFireStoreRoomListener()
    }

    override fun onStop() {
        super.onStop()
        FirestoreService.closeFireStoreRoomListener()
    }

    fun onBackButtonClick() {
        Log.d(TAG, "onBackButtonClick() called")
        finish()
    }

    fun sendMessage() {
        Log.d(TAG, "performSendMessage() called")
        val senderId = Firebase.auth.currentUser?.uid
        val receiverId = user?.userId

        val message = ChatMessage(
            senderId!!,
            receiverId!!,
            binding.chatSendMessageText.text.toString(),
            System.currentTimeMillis() / 1000
        )

        FirestoreService.sendMessage(_roomId, message)

        binding.chatSendMessageText.text.clear()
    }

    private fun findRoom() {
        val currentUser = Firebase.auth.currentUser
        val ref = FirebaseFirestore.getInstance().collection("users")
            .document(currentUser!!.uid)

        ref.get().addOnSuccessListener { userSnapshot ->
            // 현재 User의 전체 Chat Room ID 가져옴
            Log.d(TAG, "findRoom addOnSuccessListener")
            val groups = userSnapshot.data?.get("groupList") as? HashMap<String, ArrayList<String>>
            val rooms = userSnapshot.data?.get("roomList") as HashMap<String, String>

            var groupId: String? = null
            var roomId: String? = null
            if (groups != null) {
                for ((k, v) in groups) {
                    if(v.count() == 2 && v.contains(user!!.userId)) {
                        groupId = k
                        roomId = rooms[groupId]
                    }
                }
            }

            Log.d(TAG, "RoomId: $roomId")
            if (roomId.isNullOrEmpty()) {
                Log.d(TAG, "create before")
                roomId = UUID.randomUUID().toString()
                groupId = System.currentTimeMillis().toString()
                Log.d(TAG, "before create room groupId: $groupId, roomId: $roomId")
                FirestoreService.createRoom(groupId, roomId, arrayListOf(currentUser!!.uid, user!!.userId))
            }
            else {
                Log.d(TAG, "getRoom before")
                FirestoreService.getRoom(groupId!!, roomId)
            }
            _roomId = roomId
        }
    }

    // 대화방에 상대방 추가 메소드
    private fun putUserToRoom(userId: String) {
        val ref = FirebaseFirestore.getInstance().collection("users")
            .document(userId)
        ref
            .get()
            .addOnSuccessListener {
                val userInfo = it.toObject(UserInfo::class.java)
                userInfo?.roomList?.put(Firebase.auth.currentUser!!.uid, _roomId)

                val updatedUserInfo = hashMapOf(
                    "employeeNumber" to userInfo!!.employeeNumber,
                    "name" to userInfo!!.name,
                    "profileImg" to userInfo!!.profileImg,
                    "roomList" to userInfo!!.roomList,
                    "userId" to userInfo!!.userId
                )
                ref.set(updatedUserInfo)
            }
    }

    fun onToggle() {
        if (binding.chatExpandButton.isChecked) {
            binding.getImageButton.startAnimation(fab_open)
            binding.inviteButton.startAnimation(fab_open)
        } else {
            binding.getImageButton.startAnimation(fab_close)
            binding.inviteButton.startAnimation(fab_close)
        }
    }

    fun onGetImageClick() {
        getImageFromGallery()
    }

    private fun getImageFromGallery() {
        getContent.launch("image/*")
    }

    fun onInviteClick() {
        val intent = Intent(this, InviteActivity::class.java)

        intent.putExtra(USERLIST_KEY, userListInRoom)
        intent.putExtra(ROOM_KEY, _roomId)

        startForResult.launch(intent)
    }

    private fun makeGroup(users: ArrayList<String>) {
        // 단체방 새로 생성
        _roomId = UUID.randomUUID().toString()

        val groupId = UUID.randomUUID().toString()
        for (v in users) {
            userListInRoom.add(v)
        }
        val roomRef = FirebaseFirestore.getInstance().collection("rooms")
            .document(_roomId)

        roomRef.set(hashMapOf(
            "userList" to userListInRoom,
        ))

        // 사용자마다 갱신
        for (userId in userListInRoom) {
            val userRef = FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                userRef.get()
                .addOnSuccessListener { userDocument ->
                    val userInfo = userDocument.toObject(UserInfo::class.java)
                    userInfo!!.roomList.put(groupId, _roomId)

                    userRef.set(hashMapOf(
                        "userId" to userInfo.userId,
                        "name" to userInfo.name,
                        "profileImg" to userInfo.profileImg,
                        "employeeNumber" to userInfo.employeeNumber,
                        "roomList" to userInfo.roomList
                    ))
                }
        }
    }

    override fun onGetRoomComplete() {
        TODO("Not yet implemented")
    }

    override fun onGetMessage(message: ChatMessage) {
        Log.d(TAG, "onGetMessage Called")

        if(message.senderId == Firebase.auth.currentUser!!.uid) {
            groupieAdapter.add(SendMessageItem(message))
        }
        else {
            groupieAdapter.add(ReceiveMessageItem(message))
        }
        binding.chatRecyclerView.scrollToPosition(groupieAdapter.itemCount-1)
    }
}
