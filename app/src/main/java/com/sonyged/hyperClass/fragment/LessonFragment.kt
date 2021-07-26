package com.sonyged.hyperClass.fragment

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.databinding.FragmentLessonBinding
import com.sonyged.hyperClass.model.Lesson
import com.sonyged.hyperClass.utils.startLessonCreateActivity
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

        binding.course.text1.setText(R.string.course)
        binding.teacher.text1.setText(R.string.teacher)
        binding.student.text1.setText(R.string.student)

        binding.editButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.anim_edit_close
            )
        )
        binding.editButton.setOnClickListener {
            if (viewModel.isTeacher()) {
                if (binding.editButton.isSelected) {
                    extendFab()
                } else {
                    shrinkFab()
                }
            } else {
                Toast.makeText(requireContext(), "Feature is not implemented", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.deleteButton.setOnClickListener {
            Toast.makeText(requireContext(), "Feature is not implemented", Toast.LENGTH_SHORT)
                .show()
        }

        binding.changeButton.setOnClickListener {
            viewModel.lesson.value?.let {
                startLessonCreateActivity(requireContext(), it)
            }
        }

        viewModel.lesson.observe(viewLifecycleOwner) { updateLesson(it) }
    }

    private fun updateLesson(lesson: Lesson) {
        Timber.d("updateLesson - lesson: $lesson")

        binding.course.text2.text = lesson.courseName
        binding.teacher.text2.text = lesson.teacher
        binding.student.text2.text = getString(R.string.student_count_1, lesson.studentCount)

        if (!viewModel.isTeacher()) {
            if (lesson.kickUrl != null) {
                binding.editButton.visibility = View.VISIBLE
            } else {
                binding.editButton.visibility = View.GONE
            }
        }
    }

    private fun shrinkFab() {
        binding.editButton.isSelected = true
        binding.editButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.anim_edit_close
            )
        )
        if (binding.editButton.drawable is AnimatedVectorDrawable) {
            (binding.editButton.drawable as AnimatedVectorDrawable).start()
        }
        binding.changeButton.show()
        binding.deleteButton.show()
    }

    private fun extendFab() {
        binding.editButton.isSelected = false
        binding.editButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.anim_close_edit
            )
        )
        if (binding.editButton.drawable is AnimatedVectorDrawable) {
            (binding.editButton.drawable as AnimatedVectorDrawable).start()
        }
        binding.deleteButton.hide()
        binding.changeButton.hide()
    }
}