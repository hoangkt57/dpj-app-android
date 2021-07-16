package com.sonyged.hyperClass.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.StudentAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.constants.KEY_COURSE
import com.sonyged.hyperClass.databinding.ActivityStudentListBinding
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.utils.startStudentActivity
import com.sonyged.hyperClass.viewmodel.StudentListViewModel
import com.sonyged.hyperClass.viewmodel.StudentListViewModelFactory
import timber.log.Timber

class StudentListActivity : BaseActivity(), OnItemClickListener {

    private val binding: ActivityStudentListBinding by lazy {
        ActivityStudentListBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<StudentListViewModel> {
        val course = intent.getParcelableExtra(KEY_COURSE) ?: Course.empty()
        StudentListViewModelFactory(application, course)
    }

    private val adapter: StudentAdapter by lazy {
        StudentAdapter(this, viewModel.isTeacher())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        viewModel.students.observe(this) { updateStudents(it) }
    }

    private fun setupView() {
        binding.loading.show()
        binding.title.setText(R.string.student)
        binding.studentCount.text = viewModel.course.studentCount.toString()

        binding.back.setOnClickListener {
            finish()
        }

        binding.recyclerView.apply {
            adapter = this@StudentListActivity.adapter
        }
    }

    private fun updateStudents(students: List<Student>) {
        Timber.d("updateStudents - size: ${students.size}")

        binding.studentCount.text = students.size.toString()

        adapter.submitList(students)
        binding.loading.hide()
    }

    override fun onItemClick(position: Int) {
        Timber.d("onItemClick - position: $position")

        val student = adapter.getAdapterItem(position)
        startStudentActivity(this, student)

    }
}