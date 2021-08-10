package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.contract.OpenLesson
import com.sonyged.hyperClass.model.Exercise

class StudentLessonFragment : StudentExerciseFragment() {

    companion object {

        fun create(): Fragment {
            return StudentLessonFragment()
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

        adapter.getAdapterItem(position)?.let {
            openLesson.launch(it)
        }

    }

    private val openLesson = registerForActivityResult(OpenLesson()) {}
}