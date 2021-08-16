package com.example.androidchatapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.databinding.ActivityInviteBinding
import com.example.androidchatapp.models.InviteInfo
import com.example.androidchatapp.models.InviteItem
import com.example.androidchatapp.models.UserInfo
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupieAdapter

private const val TAG = "InviteActivity"
class InviteActivity : AppCompatActivity() {
    private var _binding: ActivityInviteBinding? = null
    private val binding get() = _binding!!
    private val groupieAdapter = GroupieAdapter()

    private var userList = arrayListOf<String>()
    private var roomId = ""
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

        userList =  intent.getStringArrayListExtra(USERLIST_KEY) as ArrayList<String>
        roomId = intent.getStringExtra(ROOM_KEY).toString()

        Log.d(TAG, "User List : $userList, room : $roomId")

        val ref = FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener {
                if(it != null) {
                    for (document in it!!.documentChanges) {
                        when(document.type) {
                            DocumentChange.Type.ADDED -> {
                                val userInfo = document.document.toObject(UserInfo::class.java)

                                if(!userList.contains(userInfo.userId)) {
                                    groupieAdapter.add(InviteItem(InviteInfo(false, userInfo)))
                                }
                            }
                        }
                    }
                }

                binding.inviteRecyclerView.adapter = groupieAdapter
            }
    }

    fun onInviteClick() {

    }

    fun onCancelClick() {

    }
}