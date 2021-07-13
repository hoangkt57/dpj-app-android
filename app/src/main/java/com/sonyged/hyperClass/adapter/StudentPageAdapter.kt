package com.sonyged.hyperClass.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sonyged.hyperClass.fragment.StudentFragment
import com.sonyged.hyperClass.fragment.StudentLessonFragment
import com.sonyged.hyperClass.fragment.StudentWorkoutFragment

class StudentPageAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                StudentFragment.create()
            }
            1 -> {
                StudentLessonFragment.create()
            }
            else -> {
                StudentWorkoutFragment.create()
            }
        }
    }
}