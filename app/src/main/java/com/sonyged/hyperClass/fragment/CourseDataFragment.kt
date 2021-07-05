package com.sonyged.hyperClass.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.KEY_COURSE
import com.sonyged.hyperClass.model.Course

class CourseDataFragment : BaseFragment(R.layout.fragment_course_data) {

    companion object {

        fun create(course: Course): Fragment {
            val fragment = CourseDataFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_COURSE, course)
            fragment.arguments = bundle
            return fragment
        }
    }
}