package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.constants.KEY_COURSE
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Exercise

class CourseLessonFragment : BaseExerciseFragment() {

    companion object {

        fun create(course: Course): Fragment {
            val fragment = CourseLessonFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_COURSE, course)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lessons.observe(viewLifecycleOwner) { updateLessons(it) }
    }

    private fun updateLessons(lessons: List<Exercise>) {
        updateData(lessons)
    }

    override fun onItemClick(position: Int) {
        super.onItemClick(position)

        val exercise = adapter.getAdapterItem(position)
        startLessonActivity(exercise)
    }
}