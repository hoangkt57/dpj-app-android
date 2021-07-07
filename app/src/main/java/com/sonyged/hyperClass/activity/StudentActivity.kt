package com.sonyged.hyperClass.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.StudentAdapter
import com.sonyged.hyperClass.constants.KEY_COURSE
import com.sonyged.hyperClass.databinding.ActivityStudentBinding
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.viewmodel.StudentViewModel
import com.sonyged.hyperClass.viewmodel.StudentViewModelFactory
import timber.log.Timber

class StudentActivity : BaseActivity() {

    private val binding: ActivityStudentBinding by lazy {
        ActivityStudentBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<StudentViewModel> {
        val course = intent.getParcelableExtra(KEY_COURSE) ?: Course.empty()
        StudentViewModelFactory(application, course)
    }

    private val adapter: StudentAdapter by lazy {
        StudentAdapter(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        viewModel.students.observe(this) { updateStudents(it) }
    }

    private fun setupView() {
        binding.title.setText(R.string.student)
        binding.studentCount.text = viewModel.course.studentCount.toString()

        binding.back.setOnClickListener {
            finish()
        }

        binding.recyclerView.apply {
            adapter = this@StudentActivity.adapter
        }
    }

    private fun updateStudents(students: List<Student>) {
        Timber.d("updateStudents - size: ${students.size}")

        binding.studentCount.text = students.size.toString()

        adapter.submitList(students)

    }
}