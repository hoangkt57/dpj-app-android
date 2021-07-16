package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.StudentAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.databinding.FragmentStudentListBinding
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.utils.startStudentActivity
import com.sonyged.hyperClass.viewmodel.ExerciseViewModel
import timber.log.Timber

class StudentListFragment : BaseFragment(R.layout.fragment_student_list), OnItemClickListener {

    companion object {

        fun create(): Fragment {
            return StudentListFragment()
        }
    }

    private val binding: FragmentStudentListBinding by lazy {
        FragmentStudentListBinding.bind(requireView())
    }

    private val viewModel: ExerciseViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ExerciseViewModel::class.java)
    }

    private val adapter: StudentAdapter by lazy {
        StudentAdapter(this, viewModel.isTeacher())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loading.show()
        binding.recyclerView.apply {
            adapter = this@StudentListFragment.adapter
        }

        viewModel.students.observe(viewLifecycleOwner) { updateStudents(it) }
    }

    private fun updateStudents(students: List<Student>) {
        Timber.d("updateStudents - size: ${students.size}")

        adapter.submitList(students)
        binding.loading.hide()
    }

    override fun onItemClick(position: Int) {
        Timber.d("onItemClick - position: $position")

        val student = adapter.getAdapterItem(position)
        startStudentActivity(requireContext(), student)
    }
}