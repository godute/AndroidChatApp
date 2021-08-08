package com.example.androidchatapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchatapp.ProfileActivity
import com.example.androidchatapp.adapters.GroupInfoAdapter
import com.example.androidchatapp.adapters.OnItemClick
import com.example.androidchatapp.databinding.FragmentUserListBinding
import com.example.androidchatapp.models.GroupInfo
import com.example.androidchatapp.models.SharedViewModel
import com.example.androidchatapp.models.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "UserListFragment"

class UserListFragment : Fragment(), OnItemClick {
//    private val _userList = MutableLiveData<ArrayList<GroupInfo>>()


//    private var userList = ArrayList<GroupInfo>()

    private var selectedPhotoUri: Uri? = null
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    // 데이터 공유 ViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    val userList: LiveData<ArrayList<GroupInfo>>
        get() {
            return sharedViewModel.GroupList
        }

    private val _userList = ArrayList<GroupInfo>()

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
        getUsers()

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
                            dc.document.data.apply {
                                sharedViewModel.addUser(
                                    UserInfo(
                                        get("uid") as String,
                                        get("name") as String,
                                        get("profileImg") as String,
                                        get("employeeNumber").toString().toInt()
                                    )
                                )
                            }
                        }
                        DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified: ${dc.document.data}")
                        DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed: ${dc.document.data}")
                    }
                }
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    private fun getUsers() {
        _userList.clear()

        val ref = FirebaseFirestore.getInstance().collection("users")
        val fUsers = ArrayList<UserInfo>()
        ref.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    Log.d(TAG, "DocumentSize: ${snapshot.size()}")

                    for (dc in snapshot) {
                        dc.data.apply {
                            Log.d(TAG, "DocumentSnapshot data: ${dc.data}")
                            fUsers.add(
                                UserInfo(
                                    get("uid") as String,
                                    get("name") as String,
                                    get("profileImg") as String,
                                    get("employeeNumber").toString().toInt()
                                )
                            )
                        }
                    }

                    val groupbyList = fUsers.groupBy { user->
                        user.uid == Firebase.auth.currentUser!!.uid
                    }

                    val sortedList = groupbyList.toSortedMap(compareBy<Boolean> {it}.thenBy { it })

                    for((k, v) in sortedList) {
                        if(k) {
                            _userList.add(GroupInfo("내 프로필", v))
                        }
                        else {
                            _userList.add(GroupInfo("친구 목록", v))
                        }
                    }

                    sharedViewModel.setUserList(_userList)
                    setupRecyclerView()
                } else {
                    Log.d(TAG, "No such document")
                }
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

            adapter = GroupInfoAdapter(userList?.value as List<GroupInfo>, this@UserListFragment)

        }
    }

    override fun onProfileClick(uid: String) {
        Log.d(TAG, "onProfileClick Called $uid")
//
//        val fragment = ProfileFragment()
//        val args = Bundle()
//        args.putString("userId", uid)
//        fragment.arguments = args
//        val transaction = activity?.supportFragmentManager?.beginTransaction()
//        transaction?.replace(R.id.fragmentContainer, fragment)
//        transaction?.commit()
        activity?.let{
            val intent = Intent(it, ProfileActivity::class.java)
            intent.putExtra("userId", uid)
            it.startActivity(intent)
        }
    }
}
