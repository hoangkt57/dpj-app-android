package com.sonyged.hyperClass.fragment

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.activity.BaseActivity
import com.sonyged.hyperClass.constants.EVENT_LESSON_CHANGE
import com.sonyged.hyperClass.constants.STATUS_FAILED
import com.sonyged.hyperClass.constants.STATUS_LOADING
import com.sonyged.hyperClass.constants.STATUS_SUCCESSFUL
import com.sonyged.hyperClass.contract.CreateLesson
import com.sonyged.hyperClass.contract.CreateLessonInput
import com.sonyged.hyperClass.databinding.FragmentLessonBinding
import com.sonyged.hyperClass.model.Lesson
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.observer.AppObserver
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
            extendFab()
            viewModel.lesson.value?.let {
                deleteLessonDialog(it)
            }
        }

        binding.changeButton.setOnClickListener {
            extendFab()
            viewModel.lesson.value?.let {
                createLesson.launch(CreateLessonInput(lesson = it))
            }
        }

        binding.startButton.setOnClickListener {

        }

        viewModel.lesson.observe(viewLifecycleOwner) { updateLesson(it) }
        viewModel.status.observe(viewLifecycleOwner) { updateStatus(it) }
    }

    private fun updateLesson(lesson: Lesson) {
        Timber.d("updateLesson - lesson: $lesson")

        binding.course.text2.text = lesson.courseName
        binding.teacher.text2.text = lesson.teacher.name
        binding.student.text2.text = getString(R.string.student_count_1, lesson.studentCount)

        if (!viewModel.isTeacher()) {
            if (lesson.kickUrl != null) {
                binding.startButton.visibility = View.VISIBLE
            } else {
                binding.startButton.visibility = View.GONE
            }
        } else {
            binding.editButton.visibility = View.VISIBLE
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

    private fun updateStatus(status: Status) {
        when (status.id) {
            STATUS_LOADING -> {
                if (activity is BaseActivity) {
                    (activity as BaseActivity).showProgressDialog()
                }
            }
            STATUS_FAILED -> {
                if (activity is BaseActivity) {
                    (activity as BaseActivity).hideProgressDialog()
                }
            }
            STATUS_SUCCESSFUL -> {
                activity?.let {
                    if (activity is BaseActivity) {
                        (activity as BaseActivity).hideProgressDialog()
                    }
                    Toast.makeText(it.applicationContext, R.string.deleted, Toast.LENGTH_SHORT).show()
                    AppObserver.getInstance().sendEvent(EVENT_LESSON_CHANGE)
                    it.finish()
                }
            }
        }
    }

    private fun deleteLessonDialog(lesson: Lesson) {
        val hasBatchId = lesson.batchId != null
        val builder = if (hasBatchId) {
            MaterialAlertDialogBuilder(requireContext(), R.style.DeleteLessonDialog)
        } else {
            MaterialAlertDialogBuilder(requireContext(), R.style.AddStudentDialog)
        }
        if (hasBatchId) {
            builder.setTitle(R.string.do_you_want_delete_all_lesson)
            builder.setMessage(R.string.do_you_want_delete_all_lesson_msg)
            builder.setNeutralButton(R.string.mtrl_picker_cancel) { dialog, _ ->
                dialog.dismiss()
            }
        } else {
            builder.setTitle(R.string.do_you_want_delete_lesson)
            builder.setMessage(R.string.do_you_want_delete_lesson_msg)
        }
        builder.setNegativeButton(if (!hasBatchId) R.string.no else R.string.delete_this_lesson) { dialog, _ ->
            if (hasBatchId) {
                viewModel.deleteLesson(lesson.id, false)
            }
            dialog.dismiss()
        }
        builder.setPositiveButton(if (!hasBatchId) R.string.yes else R.string.delete_all) { dialog, _ ->
            if (hasBatchId) {
                viewModel.deleteLesson(lesson.batchId, true)
            } else {
                viewModel.deleteLesson(lesson.id, false)
            }
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private val createLesson = registerForActivityResult(CreateLesson()) {}
}