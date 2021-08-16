package com.example.androidchatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.androidchatapp.databinding.ActivityTabBinding
import com.example.androidchatapp.fragment.GroupWareFragment
import com.example.androidchatapp.fragment.MoreFragment
import com.example.androidchatapp.fragment.RecentlyChatFragment
import com.example.androidchatapp.fragment.UserListFragment
import com.google.android.material.tabs.TabLayout

private const val NUM_PAGES = 5


class TabActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityTabBinding
    private val binding get() = _binding!!

    private lateinit var _currentFragmentContainer: FragmentContainerView
    private lateinit var _fragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityTabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _binding?.apply{
            tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {

                    _fragment = when(tab!!.position) {
                        0 -> UserListFragment()
                        1 -> RecentlyChatFragment()
                        2 -> GroupWareFragment()
                        else -> MoreFragment()
                    }

                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, _fragment)
                    transaction.commit()
                }
            })

            _currentFragmentContainer = fragmentContainer
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, UserListFragment())
            .commit()
    }
}