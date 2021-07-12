package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.databinding.FragmentLessonBinding
import com.sonyged.hyperClass.model.Lesson
import com.sonyged.hyperClass.viewmodel.ExerciseViewModel
import timber.log.Timber

class LessonFragment : BaseFragment(R.layout.fragment_lesson) {

    companion object {

        fun create(): Fragment {
            return LessonFragment()
        }
    }

    private val binding: FragmentLessonBinding by lazy {
        FragmentLessonBinding.bind(requireView())
    }

    private val viewModel: ExerciseViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ExerciseViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lesson.observe(viewLifecycleOwner) { updateLesson(it) }
    }

    private fun updateLesson(lesson: Lesson) {
        Timber.d("updateLesson - lesson: $lesson")

        binding.courseName.text = lesson.courseName
        binding.teacherName.text = lesson.teacher
        binding.studentCount.text = getString(R.string.student_count_1, lesson.studentCount)

        if (lesson.kickUrl != null) {
            binding.startButton.visibility = View.VISIBLE
        } else {
            binding.startButton.visibility = View.GONE
        }

    }
}