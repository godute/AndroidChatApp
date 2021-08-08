package com.example.androidchatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidchatapp.databinding.ActivityProfileBinding
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "ProfileActivity"
class ProfileActivity : AppCompatActivity() {
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityProfileBinding.inflate(layoutInflater)
        binding.apply {
            profileActivity = this@ProfileActivity
        }
        setContentView(binding.root)
    }

    override fun onStart() {
        Log.d(TAG, "onStart() called")
        super.onStart()
        val uid = intent.getStringExtra("userId").toString()
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    binding.profileUserName.text = document.data?.get("name").toString()
                }
                else {
                    Log.d(TAG, "No such document")
                }
            }
    }

    fun onChatClick() {
        Log.d(TAG, "onChatClick() called")
        val intent = Intent(this@ProfileActivity, ChatActivity::class.java)
        intent.putExtra("patnerId", "")
        startActivity(intent)
    }

    fun onBackClick() {
        Log.d(TAG, "onBackClick Called")
        finish()
    }
}