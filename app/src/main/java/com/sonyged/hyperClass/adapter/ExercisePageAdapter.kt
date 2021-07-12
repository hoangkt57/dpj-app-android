package com.sonyged.hyperClass.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sonyged.hyperClass.fragment.LessonFragment
import com.sonyged.hyperClass.fragment.StudentFragment
import com.sonyged.hyperClass.fragment.WorkoutFragment

class ExercisePageAdapter(fragmentActivity: FragmentActivity, private val isLesson: Boolean, private val isTeacher: Boolean) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return if (isTeacher) 2 else 1
    }

    override fun createFragment(position: Int): Fragment {
        return if (isTeacher) {
            if (position == 0) {
                StudentFragment.create()
            } else if (position == 1 && isLesson) {
                LessonFragment.create()
            } else {
                WorkoutFragment.create()
            }
        } else {
            if (isLesson) {
                LessonFragment.create()
            } else {
                WorkoutFragment.create()
            }
        }

    }
}