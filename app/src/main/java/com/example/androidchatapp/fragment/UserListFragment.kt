package com.example.androidchatapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.ProfileActivity
import com.example.androidchatapp.adapters.GroupInfoAdapter
import com.example.androidchatapp.adapters.OnItemClick
import com.example.androidchatapp.databinding.FragmentUserListBinding
import com.example.androidchatapp.models.SharedViewModel
import com.example.androidchatapp.models.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

private const val TAG = "UserListFragment"

class UserListFragment : Fragment(), OnItemClick {
    companion object {
        lateinit var CurrentUser: UserInfo
    }

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    // 데이터 공유 ViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "OnCreateView Called")
        _binding = FragmentUserListBinding.inflate(inflater, container, false)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            userListFragment = this@UserListFragment
        }

        fetchUsers()

        return binding.root
    }

    private fun fetchUsers() {
        sharedViewModel.initGroup()

        val ref = FirebaseFirestore.getInstance().collection("users")

        ref.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                Log.d(TAG, "Current data: ${snapshot.metadata}")
                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d(TAG, "New User: ${dc.document.data}")
                            val userInfo = dc.document.toObject(UserInfo::class.java)

                            val groupName: String = if(userInfo.userId == Firebase.auth.currentUser!!.uid) {
                                sharedViewModel.setCurrentUser(userInfo)
                                "내 프로필"
                            } else {
                                "친구목록"
                            }
                            sharedViewModel.addUser(groupName, userInfo)
                        }
                        DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified: ${dc.document.data}")
                        DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed: ${dc.document.data}")
                    }
                }
            } else {
                Log.d(TAG, "Current data: null")
            }
            setupRecyclerView()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated Called, User Id: ${Firebase.auth.currentUser?.uid.toString()}")
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView Called")
        binding.groupListRecyclerView.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(requireActivity())

            adapter = sharedViewModel.GroupList.value?.let { GroupInfoAdapter(it, this@UserListFragment) }
        }
    }

    override fun onProfileClick(uid: String) {
        Log.d(TAG, "onProfileClick Called $uid")

        activity?.let {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    document?.apply {
                        Log.d(TAG, "Success get User ${get("userId").toString()}")

                        val intent = Intent(it, ProfileActivity::class.java)

                        val userInfo = document.toObject(UserInfo::class.java)
                        intent.putExtra(ProfileActivity.USER_KEY, userInfo)
                        it.startActivity(intent)
                    }
                }
        }
    }
}
