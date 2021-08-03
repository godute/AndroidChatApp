package com.example.androidchatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.androidchatapp.adapters.TabFragmentStateAdapter
import com.example.androidchatapp.databinding.ActivityTabBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val NUM_PAGES = 5


class TabActivity : AppCompatActivity() {
    private val tabIconList = arrayListOf(
        R.drawable.ic_user,
        R.drawable.ic_chat,
        R.drawable.ic_groupware,
        R.drawable.ic_more
    )

    private lateinit var _binding: ActivityTabBinding
    private val binding get() = _binding!!

    private lateinit var _viewPager: ViewPager2
    private lateinit var _tabLayout: TabLayout

    private lateinit var mPager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityTabBinding.inflate(layoutInflater)

        setContentView(binding.root)

        init()
    }

    private fun init() {
        _binding.viewPager2.adapter = TabFragmentStateAdapter(this)
        TabLayoutMediator(_binding.tabLayout, _binding.viewPager2) { tab, position ->
            tab.setIcon(tabIconList[position])
        }.attach()
    }

}