package com.example.androidchatapp

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.InviteActivity.Companion.ROOM_KEY
import com.example.androidchatapp.InviteActivity.Companion.USERLIST_KEY
import com.example.androidchatapp.databinding.ActivityChatBinding
import com.example.androidchatapp.fragment.RecentlyChatFragment
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

    private var groupId: String? = null
    private var roomId: String? = null
    private var recentRoomInfo: RecentChatRoom? = null

    private var userListInRoom: ArrayList<String> = arrayListOf()

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
        Log.d(TAG, "$uri")
        val inputStream = applicationContext.contentResolver.openInputStream(uri!!)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        binding.chatImagePreview.visibility = View.VISIBLE
        binding.chatImagePreviewClose.visibility = View.VISIBLE
        binding.chatImagePreview.setImageBitmap(bitmap)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Get result")
            val intent = result.data
            val invitedUserList = intent?.getStringArrayListExtra(INVITED_USER_LIST)
            Log.d(TAG, "Invited user : $invitedUserList")

            if (invitedUserList != null) {
                inviteUsers(invitedUserList)
            }
        }
    }

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

        fab_open = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)

        try {
            user = intent.getParcelableExtra(ProfileActivity.USER_KEY) ?: null
            recentRoomInfo = intent.getParcelableExtra(RecentlyChatFragment.RECENT_MESSAGE) ?: null

            Log.d(TAG, user!!.name)

        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }

        FirestoreService.setChatActivity(this)
        FirestoreService.setOnFireStoreRoomListener(this)

        if(recentRoomInfo == null) {
            findRoomByUserInfo()
        }
        else {
            roomId = recentRoomInfo!!.roomId
            groupId = recentRoomInfo!!.groupId

            findRoomByRoomInfo()
        }
    }

    override fun onPause() {
        super.onPause()
        groupieAdapter.clear()
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

        val message = ChatMessage(
            senderId!!,
            binding.chatSendMessageText.text.toString(),
            System.currentTimeMillis() / 1000
        )

        FirestoreService.sendMessage(roomId!!, message)

        binding.chatSendMessageText.text.clear()
    }

    private fun findRoomByUserInfo() {
        val currentUser = Firebase.auth.currentUser
        val ref = FirebaseFirestore.getInstance().collection("users")
            .document(currentUser!!.uid)

        ref.get().addOnSuccessListener { userSnapshot ->
            // 현재 User의 전체 Chat Room ID 가져옴
            Log.d(TAG, "findRoom addOnSuccessListener")
            val groups = userSnapshot.data?.get("groupList") as? HashMap<String, ArrayList<String>>
            val rooms = userSnapshot.data?.get("roomList") as? HashMap<String, String> ?: HashMap()

            roomId = null
            if (groups != null) {
                for ((k, v) in groups) {
                    if(v.count() == 2 && v.contains(user!!.userId)) {
                        groupId = k
                        roomId = rooms[groupId]
                    }
                }
            }

            Log.d(TAG, "RoomId: $recentRoomInfo")
            if (roomId.isNullOrEmpty()) {
                Log.d(TAG, "create before")
                roomId = UUID.randomUUID().toString()
                groupId = System.currentTimeMillis().toString()
                Log.d(TAG, "before create room groupId: $groupId, roomId: $recentRoomInfo")
                FirestoreService.createRoom(groupId!!, roomId!!, arrayListOf(currentUser!!.uid, user!!.userId))
            }
            else {
                Log.d(TAG, "getRoom before")
                FirestoreService.getRoom(groupId!!, roomId!!)
            }
        }
    }

    private fun findRoomByRoomInfo() {
        Log.d(TAG, "findRoomByRoomInfo Called")
        FirestoreService.getRoom(groupId!!, roomId!!)
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
        intent.putExtra(ROOM_KEY, recentRoomInfo)

        startForResult.launch(intent)
    }

    fun closePreview() {
        Log.d(TAG, "closePreivew() Called")
        binding.chatImagePreview.visibility = View.GONE
        binding.chatImagePreviewClose.visibility = View.GONE
    }

    private fun inviteUsers(userList: ArrayList<String>) {
        groupId?.let { FirestoreService.addUserToRoom(it, roomId!!, userList) }
    }

    override fun onGetRoomComplete(userList: ArrayList<String>) {
        Log.d(TAG, "onGetRoomComplete($userList) Called")
        userListInRoom.clear()
        userListInRoom.addAll(userList)
    }

    override fun onGetMessage(message: ChatMessage) {
        Log.d(TAG, "onGetMessage Called")

        if(message.senderId == Firebase.auth.currentUser!!.uid) {
            when (message.type) {
                MessageType.TEXT -> groupieAdapter.add(SendMessageItem(message))
                MessageType.IMAGE -> groupieAdapter.add(SendMessageItem(message))
            }
        }
        else {
            when(message.type) {
                MessageType.TEXT -> groupieAdapter.add(ReceiveMessageItem(message))
                MessageType.IMAGE -> groupieAdapter.add(ReceiveMessageItem(message))
            }
        }
        binding.chatRecyclerView.scrollToPosition(groupieAdapter.itemCount-1)
    }

}
