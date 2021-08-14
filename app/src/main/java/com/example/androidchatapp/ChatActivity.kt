package com.example.androidchatapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.databinding.ActivityChatBinding
import com.example.androidchatapp.fragment.UserListFragment.Companion.CurrentUser
import com.example.androidchatapp.models.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupieAdapter
import java.util.*
import kotlin.collections.HashMap

private const val TAG = "ChatActivity"

class ChatActivity : AppCompatActivity() {
    private var user: UserInfo? = null
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!
    val groupieAdapter = GroupieAdapter()
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

        try {
            user = intent.getParcelableExtra<UserInfo>(ProfileActivity.USER_KEY)
            Log.d(TAG, user!!.name)

        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
        findRoom()

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
        groupieAdapter.add(SendMessageItem(message))
        binding.chatRecyclerView.adapter = groupieAdapter
    }

    private fun listenForMessages() {
        val roomRef = FirebaseFirestore.getInstance().collection("/rooms").document(_roomId)

        roomRef.collection("messages")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d(TAG, "Listen failed", e)
                    return@addSnapshotListener
                }

                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        // 메세지가 Cloud Firestore에 추가된 경우
                        DocumentChange.Type.ADDED -> {
                            val message = dc.document.toObject(ChatMessage::class.java)
                            // sender와 receiver 파악
                            if (message.senderId == Firebase.auth.currentUser!!.uid) {
                                groupieAdapter.add(SendMessageItem(message))
                            } else {
                                groupieAdapter.add(ReceiveMessageItem(message))
                            }

                            binding.chatRecyclerView.adapter = groupieAdapter

                        }
                    }
                }
            }
    }


    // 현재 대화할 상대의 Room 가져옴
    private fun getRoom(roomId: String) {
        val roomRef = FirebaseFirestore.getInstance().collection("rooms")
            .document(roomId)

                val messageRef = roomRef.collection("messages")
            .get()
            .addOnSuccessListener { snapshots ->
                val messageList = ArrayList<ChatMessage>()
                for(docu in snapshots) {
                    messageList.add(docu.toObject(ChatMessage::class.java))
                }
                if(messageList.isNotEmpty()) {
                    setRoom(ChatRoom(_roomId, ArrayList<String>(), ChatMessageList(messageList)))

                }
            }
    }

    private fun setRoom(chatRoom: ChatRoom) {
        // Room 초기세팅
        chatRoom.chatMessageList.chatMessages.map {
            when (it.senderId) {
                Firebase.auth.currentUser!!.uid -> groupieAdapter.add(SendMessageItem(it))
                else -> groupieAdapter.add(ReceiveMessageItem(it))
            }
        }

        listenForMessages()

        binding.chatRecyclerView.adapter = groupieAdapter
    }

    private fun findRoom() {
        val currentUser = Firebase.auth.currentUser
        val ref = FirebaseFirestore.getInstance().collection("users")
            .document(currentUser!!.uid)

        ref.get().addOnSuccessListener { userSnapshot ->
            // 현재 User의 전체 Chat Room ID 가져옴
            val rooms = userSnapshot.data?.get("roomList") as HashMap<String, String>
            val roomId = rooms.get(user?.userId)

            if (roomId != null) {
                // 해당 채팅상대에 대한 room Id가 존재하면, room(대화방) 정보를 가져온다
                getRoom(roomId)
            } else {
                // 상대방과 대화흔적이 없으면 새로 생성한다.
                _roomId = UUID.randomUUID().toString()
                rooms.put(user!!.userId, _roomId)
                ref.set(hashMapOf(
                    "employeeNumber" to CurrentUser.employeeNumber,
                    "name" to CurrentUser.name,
                    "profileImg" to CurrentUser.profileImg,
                    "roomList" to rooms,
                    "userId" to CurrentUser.userId
                ))
                putUserToRoom()
            }
        }
    }

    // 대화방에 상대방 추가 메소드
    private fun putUserToRoom() {
        val ref = FirebaseFirestore.getInstance().collection("users")
            .document(user!!.userId)
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



    private fun findRoom2() {
        val currentUser = Firebase.auth.currentUser
        val ref = FirebaseFirestore.getInstance().collection("users")
            .document(currentUser!!.uid)
            .get()
            .addOnSuccessListener { userSnapshot ->
                // 현재 User의 Chat Room ID 가져옴
                val rooms = userSnapshot.data?.get("roomList") as ArrayList<String>

                for (room in rooms) {
                    val roomRef = FirebaseFirestore.getInstance().collection("rooms")
                        .document(room)
                        .get()
                        .addOnSuccessListener { roomSnapshot ->
                            val roomUsers = roomSnapshot.data?.get("userList") as ArrayList<String>

                            if (roomUsers.count() == 2 && roomUsers.contains(user!!.userId)) {
                                _roomId = room
                            }
                        }
                }
            }


    }

    private fun getMessageList() {
        FirebaseFirestore.getInstance().collection("/MessageGroup").document(user!!.userId)
            .get()
            .addOnSuccessListener {
                if (it != null) {

                    _roomId = it.data?.get("roomId").toString()
                    FirebaseFirestore.getInstance().collection("/Room").document(_roomId)
                        .get()
                        .addOnSuccessListener { docu ->
                            val messages = HashMap(docu.data)
                            for ((k, v) in messages) {

                            }
                        }
                }
            }
    }

}
