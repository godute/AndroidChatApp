package com.example.androidchatapp.fragment

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
import com.example.androidchatapp.R
import com.example.androidchatapp.adapters.GroupInfoAdapter
import com.example.androidchatapp.adapters.OnItemClick
import com.example.androidchatapp.databinding.FragmentUserListBinding
import com.example.androidchatapp.models.GroupInfo
import com.example.androidchatapp.models.SharedViewModel
import com.example.androidchatapp.models.UserInfo
import com.google.firebase.auth.ktx.auth
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
    val userList: LiveData<ArrayList<GroupInfo>> get() {
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
        addUsersTest()

        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated Called, User Id: ${Firebase.auth.currentUser?.uid.toString()}")
        super.onViewCreated(view, savedInstanceState)


    }

    // 임시 테스트용 함수
    private fun addUsersTest() {
        Log.d(TAG, "addUsersTest Called")
        sharedViewModel.clearGroupList()
        val fCategory = "내 프로필"
        val fUsers = ArrayList<UserInfo>()
        fUsers.add(UserInfo("정우태", ""))

        val sCategory = "내 부서"
        val sUsers = ArrayList<UserInfo>()
        sUsers.add(UserInfo("유재석", ""))
        sUsers.add(UserInfo("송중기", ""))
        sUsers.add(UserInfo("브래드피트", ""))

        _userList.add(GroupInfo(fCategory, fUsers))
        _userList.add(GroupInfo(sCategory, sUsers))

        sharedViewModel.setUserList(_userList)
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView Called")
        binding.groupListRecyclerView.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(requireActivity())

            adapter = GroupInfoAdapter(userList.value as List<GroupInfo>, this@UserListFragment)

        }
    }

    override fun onProfileClick(uid: String) {
        Log.d(TAG, "onProfileClick Called $uid")

        val fragment = ProfileFragment()
        val args = Bundle()
        args.putString("userId", uid)
        fragment.arguments =args
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragmentContainer, fragment)
        transaction?.commit()
    }
}
