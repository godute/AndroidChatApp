package com.example.androidchatapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.ChatActivity.Companion.INVITED_USER_LIST
import com.example.androidchatapp.adapters.OnInviteCheck
import com.example.androidchatapp.databinding.ActivityInviteBinding
import com.example.androidchatapp.models.InviteInfo
import com.example.androidchatapp.models.InviteItem
import com.example.androidchatapp.models.UserInfo
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupieAdapter

private const val TAG = "InviteActivity"

class InviteActivity : AppCompatActivity(), OnInviteCheck {
    private var _binding: ActivityInviteBinding? = null
    private val binding get() = _binding!!
    private val groupieAdapter = GroupieAdapter()

    private var userList = arrayListOf<String>()
    private var roomId = ""
    private var checkedUserList = arrayListOf<String>()

    companion object {
        val USERLIST_KEY = "USER_LIST"
        val ROOM_KEY = "ROOM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInviteBinding.inflate(layoutInflater)
        binding.apply {
            inviteActivity = this@InviteActivity
            inviteRecyclerView.apply {
                setHasFixedSize(true)

                layoutManager = LinearLayoutManager(context)

                adapter = groupieAdapter

            }
        }
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        userList = intent.getStringArrayListExtra(USERLIST_KEY) as ArrayList<String>
        roomId = intent.getStringExtra(ROOM_KEY).toString()

        Log.d(TAG, "User List : $userList, room : $roomId")

        FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener {
                if (it != null) {
                    for (document in it.documentChanges) {
                        when (document.type) {
                            DocumentChange.Type.ADDED -> {
                                Log.d(TAG, "Document ADDED")
                                val userInfo = document.document.toObject(UserInfo::class.java)

                                if (!userList.contains(userInfo.userId)) {
                                    groupieAdapter.add(
                                        InviteItem(
                                            InviteInfo(false, userInfo),
                                            this@InviteActivity
                                        )
                                    )
                                }
                            }
                            DocumentChange.Type.MODIFIED -> {
                                Log.d(TAG, "Document MODIFIED")

                            }
                            DocumentChange.Type.REMOVED -> {
                                Log.d(TAG, "Document REMOVED")

                            }
                        }
                    }
                }

                binding.inviteRecyclerView.adapter = groupieAdapter
            }
    }


    fun onInviteClick() {
        Log.d(TAG, "onInviteClick Called")
        val intent = Intent()
        intent.putExtra(INVITED_USER_LIST, checkedUserList)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun onCancelClick() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onCheck(userId: String) {
        Log.d(TAG, "onCheck ($userId) Called")
        checkedUserList.add(userId)
    }

    override fun onUncheck(userId: String) {
        Log.d(TAG, "onUncheck ($userId) Called")
        checkedUserList.remove(userId)
    }
}