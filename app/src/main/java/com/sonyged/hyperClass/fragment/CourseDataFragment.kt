package com.sonyged.hyperClass.fragment

import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.R

class CourseDataFragment : BaseFragment(R.layout.fragment_course_data) {

    companion object {

        fun create(): Fragment {
            return CourseDataFragment()
        }
    }
}