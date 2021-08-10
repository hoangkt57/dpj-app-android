package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.contract.OpenLesson
import com.sonyged.hyperClass.model.BaseItem
import com.sonyged.hyperClass.model.ExerciseFilter
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
        viewModel.lessonFilter.observe(viewLifecycleOwner) { updateDateRange(it) }
    }

    private fun updateLessons(lessons: List<BaseItem>) {
        updateData(lessons)
    }

    private fun updateDateRange(filter: ExerciseFilter) {
        val dateRange = filter.dateRange ?: return
        binding.date.text = getString(R.string.lesson_date_range, formatDate1(dateRange.first), formatDate1(dateRange.second))
    }

    override fun onItemClick(position: Int) {
        super.onItemClick(position)

        adapter.getAdapterItem(position)?.let {
            openLesson.launch(it)
        }
    }

    override fun showMore() {
        viewModel.loadMoreLessons()
    }

    private val openLesson = registerForActivityResult(OpenLesson()) {}
}