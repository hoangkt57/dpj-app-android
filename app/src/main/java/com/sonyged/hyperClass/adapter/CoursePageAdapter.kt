package com.sonyged.hyperClass.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sonyged.hyperClass.fragment.CourseDataFragment
import com.sonyged.hyperClass.fragment.CourseLessonFragment
import com.sonyged.hyperClass.fragment.CourseWorkoutFragment
import com.sonyged.hyperClass.model.Course

class CoursePageAdapter(fragmentActivity: FragmentActivity, private val course: Course) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                CourseLessonFragment.create(course)
            }
            1 -> {
                CourseDataFragment.create(course)
            }
            else -> {
                CourseWorkoutFragment.create(course)
            }
        }
    }
}