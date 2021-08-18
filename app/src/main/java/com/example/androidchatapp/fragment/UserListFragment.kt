package com.example.androidchatapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidchatapp.ProfileActivity
import com.example.androidchatapp.adapters.GroupInfoAdapter
import com.example.androidchatapp.adapters.OnItemClick
import com.example.androidchatapp.databinding.FragmentUserListBinding
import com.example.androidchatapp.models.SharedViewModel
import com.example.androidchatapp.models.UserInfo
import com.example.androidchatapp.services.FirestoreGetAllUserListener
import com.example.androidchatapp.services.FirestoreGetUserListener
import com.example.androidchatapp.services.FirestoreService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "UserListFragment"

class UserListFragment : Fragment(), OnItemClick, FirestoreGetAllUserListener, FirestoreGetUserListener {
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    // 데이터 공유 ViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "OnCreateView Called")
        _binding = FragmentUserListBinding.inflate(inflater, container, false)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            userListFragment = this@UserListFragment
        }

        FirestoreService.setOnFireStoreGetUserListner(this)

        FirestoreService.setOnFireStoreGetAllUserListener(this)

        fetchUsers()

        return binding.root
    }

    private fun fetchUsers() {
        FirestoreService.fetchUsers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated Called, User Id: ${Firebase.auth.currentUser?.uid.toString()}")
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView Called")
        binding.groupListRecyclerView.apply {
            setHasFixedSize(true)

            adapter =
                SharedViewModel.GroupList.value?.let { GroupInfoAdapter(it, this@UserListFragment) }
        }
    }

    override fun onProfileClick(uid: String) {
        Log.d(TAG, "onProfileClick Called $uid")
        FirestoreService.getUser(uid)
    }

    override fun onGetAllUserComplete() {
        setupRecyclerView()
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onGetUserComplete(userInfo: UserInfo) {
        activity?.let {
            val intent = Intent(it, ProfileActivity::class.java)
            intent.putExtra(ProfileActivity.USER_KEY, userInfo)
            it.startActivity(intent)
        }
    }
}
