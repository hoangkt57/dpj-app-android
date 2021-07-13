package com.sonyged.hyperClass.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sonyged.hyperClass.fragment.CourseDataFragment
import com.sonyged.hyperClass.fragment.CourseLessonFragment
import com.sonyged.hyperClass.fragment.CourseWorkoutFragment

class CoursePageAdapter(fragmentActivity: FragmentActivity, private val isTeacher: Boolean) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return if (isTeacher) 3 else 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            CourseLessonFragment.create()
        } else if (position == 1 && isTeacher) {
            CourseDataFragment.create()
        } else {
            CourseWorkoutFragment.create()
        }
    }
}