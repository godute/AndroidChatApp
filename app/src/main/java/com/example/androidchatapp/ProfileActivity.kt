package com.example.androidchatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidchatapp.databinding.ActivityProfileBinding
import com.example.androidchatapp.models.UserInfo

private const val TAG = "ProfileActivity"
class ProfileActivity : AppCompatActivity() {
    private var user: UserInfo? = null
    companion object {
        val USER_KEY = "USER_KEY"
    }
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
        user = intent.getParcelableExtra(USER_KEY)

        binding.profileUserName.text = user?.name
    }

    fun onChatClick() {
        Log.d(TAG, "onChatClick() called")
        val intent = Intent(this@ProfileActivity, ChatActivity::class.java)
        intent.putExtra(USER_KEY, user)
        startActivity(intent)
    }

    fun onBackClick() {
        Log.d(TAG, "onBackClick Called")
        finish()
    }
}