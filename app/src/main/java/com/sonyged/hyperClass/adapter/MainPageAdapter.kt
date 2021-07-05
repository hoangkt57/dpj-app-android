package com.sonyged.hyperClass.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sonyged.hyperClass.fragment.CoursePageFragment
import com.sonyged.hyperClass.fragment.HomePageFragment
import com.sonyged.hyperClass.fragment.SettingPageFragment

class MainPageAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                HomePageFragment.create()
            }
            1 -> {
                CoursePageFragment.create()
            }
            else -> {
                SettingPageFragment.create()
            }
        }
    }
}