package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.contract.OpenLesson
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.utils.formatDate1

class CourseLessonFragment : BaseExerciseFragment() {

    companion object {

        fun create(): Fragment {
            return CourseLessonFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lessons.observe(viewLifecycleOwner) { updateLessons(it) }
        viewModel.lessonDateRange.observe(viewLifecycleOwner) { updateDateRange(it) }
    }

    private fun updateLessons(lessons: List<Exercise>) {
        updateData(lessons)
    }

    private fun updateDateRange(dateRange: Pair<Long, Long>) {
        binding.date.text = getString(R.string.lesson_date_range, formatDate1(dateRange.first), formatDate1(dateRange.second))
    }

    override fun onItemClick(position: Int) {
        super.onItemClick(position)

        val exercise = adapter.getAdapterItem(position)
        openLesson.launch(exercise)
    }

    private val openLesson = registerForActivityResult(OpenLesson()) {}
}