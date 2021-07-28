package com.sonyged.hyperClass.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.ChooseUserAdapter
import com.sonyged.hyperClass.adapter.StudentAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnDeleteClickListener
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.constants.KEY_COURSE_ID
import com.sonyged.hyperClass.constants.KEY_NEW_STUDENT_COUNT
import com.sonyged.hyperClass.constants.KEY_TEACHER_ID
import com.sonyged.hyperClass.databinding.ActivityStudentListBinding
import com.sonyged.hyperClass.databinding.DialogStudentAdditionBinding
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.utils.startStudentActivity
import com.sonyged.hyperClass.viewmodel.StudentListViewModel
import com.sonyged.hyperClass.viewmodel.StudentListViewModelFactory
import timber.log.Timber

class StudentListActivity : BaseActivity(), OnItemClickListener, OnDeleteClickListener {

    private val binding: ActivityStudentListBinding by lazy {
        ActivityStudentListBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<StudentListViewModel> {
        val courseId = intent.getStringExtra(KEY_COURSE_ID) ?: ""
        val teacherId = intent.getStringExtra(KEY_TEACHER_ID) ?: ""
        StudentListViewModelFactory(application, courseId, teacherId)
    }

    private val adapter: StudentAdapter by lazy {
        StudentAdapter(this, this, viewModel.isTeacher(), viewModel.isOwner())
    }

    private val filterAdapter: ChooseUserAdapter by lazy {
        ChooseUserAdapter(null, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        viewModel.students.observe(this) { updateStudents(it) }
        viewModel.filterStudents.observe(this) { updateFilterStudents(it) }
    }

    private fun setupView() {
        binding.loading.show()
        binding.title.setText(R.string.student)

        if (!viewModel.isOwner()) {
            binding.add.visibility = View.GONE
            binding.divider.visibility = View.GONE
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.recyclerView.apply {
            adapter = this@StudentListActivity.adapter
        }

        binding.add.setOnClickListener {
            addStudentDialog()
        }
    }

    private fun updateStudents(students: List<Student>) {
        Timber.d("updateStudents - size: ${students.size}")

        binding.studentCount.text = students.size.toString()
        if (adapter.itemCount != 0 && adapter.itemCount != students.size) {
            intent.putExtra(KEY_NEW_STUDENT_COUNT, students.size)
            setResult(Activity.RESULT_OK, intent)
        }
        adapter.submitList(students)
        binding.loading.hide()

    }

    override fun onItemClick(position: Int) {
        Timber.d("onItemClick - position: $position")

        val student = adapter.getAdapterItem(position)
        startStudentActivity(this, student)

    }

    override fun onDeleteClick(position: Int) {
        Timber.d("onDeleteClick - position: $position")

        val item = adapter.getAdapterItem(position)
        deleteStudentDialog(item)

    }

    private fun addStudentDialog() {

        val viewBinding = DialogStudentAdditionBinding.inflate(layoutInflater)

        viewModel.itemSelected.clear()
        filterAdapter.setItemSelected(viewModel.itemSelected)
        viewBinding.recyclerView.adapter = filterAdapter
        filterAdapter.submitList(viewModel.allStudents)

        viewBinding.search.setOnClickListener {
            viewModel.filterStudent(viewBinding.nameEdit.text.toString())
        }

        val dialog = MaterialAlertDialogBuilder(this, R.style.AddStudentDialog)
            .setTitle(R.string.addition_of_student)
            .setView(viewBinding.root)
            .setNegativeButton(R.string.mtrl_picker_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.addition) { dialog, _ ->
                viewModel.addStudent()
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun updateFilterStudents(filterStudents: List<Student>) {
        filterAdapter.submitList(filterStudents)
    }

    private fun deleteStudentDialog(student: Student) {
        val dialog = MaterialAlertDialogBuilder(this, R.style.AddStudentDialog)
            .setTitle(R.string.delete_student)
            .setMessage(getString(R.string.delete_student_msg, student.name))
            .setNegativeButton(R.string.mtrl_picker_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.mtrl_picker_confirm) { dialog, _ ->
                viewModel.deleteStudent(student.id)
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}