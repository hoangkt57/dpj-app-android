package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.KEY_LESSON_ID
import com.sonyged.hyperClass.databinding.ActivityLessonBinding
import com.sonyged.hyperClass.model.Lesson
import com.sonyged.hyperClass.viewmodel.LessonViewModel
import com.sonyged.hyperClass.viewmodel.LessonViewModelFactory
import timber.log.Timber

class LessonActivity : BaseActivity() {

    private val binding: ActivityLessonBinding by lazy {
        ActivityLessonBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<LessonViewModel> {
        val lessonId = intent.getStringExtra(KEY_LESSON_ID) ?: ""
        LessonViewModelFactory(application, lessonId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        viewModel.lesson.observe(this) { updateLesson(it) }
    }

    private fun setupView() {

        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun updateLesson(lesson: Lesson) {
        Timber.d("updateLesson - lesson: $lesson")

        binding.title.text = lesson.name
        binding.date.text = lesson.date
        binding.teacher.text = lesson.teacher

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