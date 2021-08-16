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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupieAdapter
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "ChatActivity"

class ChatActivity : AppCompatActivity() {
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
        findRoom(_roomId)

    }

    fun onBackButtonClick() {
        Log.d(TAG, "onBackButtonClick() called")
        finish()
    }

    fun sendMessage() {
        Log.d(TAG, "performSendMessage() called")
        val senderId = Firebase.auth.currentUser?.uid
        val receiverId = user?.userId

        // 1. Room collection
        val roomRef = FirebaseFirestore.getInstance().collection("/rooms")
            .document(_roomId)

        // 2. 해당 Room 내의 Message collection내에 document 새로 생성
        val messageRef = roomRef.collection("messages")
            .document()

        val message = ChatMessage(
            senderId!!,
            receiverId!!,
            binding.chatSendMessageText.text.toString(),
            System.currentTimeMillis() / 1000
        )

        messageRef.set(message)
            .addOnSuccessListener {
                Log.d(TAG, "Success Send Message")
            }

        binding.chatSendMessageText.text.clear()
    }

    private fun listenForMessages() {
        Log.d(TAG, "Room Id : $_roomId")
        val roomRef = FirebaseFirestore.getInstance().collection("rooms").document(_roomId)

        roomRef.collection("messages").orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d(TAG, "Listen failed", e)
                    return@addSnapshotListener
                }
                var lastMessage: ChatMessage = ChatMessage()
                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        // 메세지가 Cloud Firestore에 추가된 경우
                        DocumentChange.Type.ADDED -> {
                            val message = dc.document.toObject(ChatMessage::class.java)
                            lastMessage = message
                            // sender와 receiver 파악
                            if (message.senderId == Firebase.auth.currentUser!!.uid) {
                                groupieAdapter.add(SendMessageItem(message))
                            } else {
                                groupieAdapter.add(ReceiveMessageItem(message))
                            }

                        }
                        else -> {

                        }
                    }
                }

                roomRef.get().addOnSuccessListener {
                    val userList = it.data?.get("userList") as ArrayList<*>
                    val recentMessage =
                        RecentChatMessage(user!!.name, user!!.profileImg, lastMessage.content)

                    roomRef.set(
                        hashMapOf(
                            "recentMessage" to recentMessage,
                            "userList" to userList,
                            "timestamp" to lastMessage.timestamp
                        )
                    )

                }

                binding.chatRecyclerView.scrollToPosition(groupieAdapter.itemCount - 1)
                binding.chatRecyclerView.adapter = groupieAdapter
            }
    }

    // 현재 대화할 상대의 Room 가져옴
    private fun getRoom(roomId: String) {
        val roomRef = FirebaseFirestore.getInstance().collection("rooms")
            .document(roomId)

        roomRef.collection("messages")
            .get()
            .addOnSuccessListener { snapshots ->
                val messageList = ArrayList<ChatMessage>()
                Log.d(TAG, "snapshot count : ${snapshots.count()}")

                for (docu in snapshots) {
                    messageList.add(docu.toObject(ChatMessage::class.java))
                }

                listenForMessages()
            }

        roomRef.addSnapshotListener { snapshot, e ->
            if(e != null) {
                Log.d(TAG, "Snapshot listen failed")
                return@addSnapshotListener
            }

            if(snapshot != null) {
                userListInRoom = snapshot.data?.get("userList") as ArrayList<String>

            }
        }
    }

    private fun findRoom(roomId: String) {
        val currentUser = Firebase.auth.currentUser
        val ref = FirebaseFirestore.getInstance().collection("users")
            .document(currentUser!!.uid)

        ref.get().addOnSuccessListener { userSnapshot ->
            // 현재 User의 전체 Chat Room ID 가져옴
            val rooms = userSnapshot.data?.get("roomList") as HashMap<String, String>
            val roomId = rooms.get(user?.userId)

            if (roomId != null) {
                // 해당 채팅상대에 대한 room Id가 존재하면, room(대화방) 정보를 가져온다
                _roomId = roomId
            } else {
                // 상대방과 대화흔적이 없으면 새로 생성한다.
                _roomId = UUID.randomUUID().toString()
                rooms.put(user!!.userId, _roomId)
                ref.set(
                    hashMapOf(
                        "employeeNumber" to SharedViewModel.CurrentUser.employeeNumber,
                        "name" to SharedViewModel.CurrentUser.name,
                        "profileImg" to SharedViewModel.CurrentUser.profileImg,
                        "roomList" to rooms,
                        "userId" to SharedViewModel.CurrentUser.userId
                    )
                )
                putUserToRoom(user!!.userId)
            }

            getRoom(_roomId)
        }
    }

    // room에 userList추가
    private fun roomUserUpdate() {
        val userList = listOf(SharedViewModel.CurrentUser.userId, user?.userId)
        val ref = FirebaseFirestore.getInstance().collection("rooms")
            .document(_roomId)

        ref.set(
            hashMapOf(
                "userList" to userList
            )
        )
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

                roomUserUpdate()
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

    private fun roomUpdate() {

    }
}
