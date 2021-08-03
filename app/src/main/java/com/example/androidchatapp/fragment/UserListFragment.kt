package com.example.androidchatapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.androidchatapp.adapters.GroupInfoAdapter
import com.example.androidchatapp.databinding.FragmentUserListBinding
import com.example.androidchatapp.models.GroupInfo
import com.example.androidchatapp.models.UserInfo

private const val TAG = "UserListFragment"
class UserListFragment : Fragment() {
    private val userList = ArrayList<GroupInfo>()


    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "OnCreateView Called")
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        addUsersTest()
        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated Called")
        super.onViewCreated(view, savedInstanceState)
    }

    // 임시 테스트용 함수
    private fun addUsersTest() {
        Log.d(TAG, "addUsersTest Called")
        val fCategory = "내 프로필"
        val fUsers = ArrayList<UserInfo>()
        fUsers.add(UserInfo("정우태", ""))

        val sCategory = "내 부서"
        val sUsers = ArrayList<UserInfo>()
        sUsers.add(UserInfo("유재석", ""))
        sUsers.add(UserInfo("송중기", ""))
        sUsers.add(UserInfo("브래드피트", ""))

        userList.add(GroupInfo(fCategory, fUsers))
        userList.add(GroupInfo(sCategory, sUsers))
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView Called")
        binding.groupListRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = GroupInfoAdapter(userList)

        }
    }
}