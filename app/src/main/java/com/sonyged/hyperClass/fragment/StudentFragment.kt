package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.databinding.FragmentStudentBinding
import com.sonyged.hyperClass.databinding.ViewChipTagBinding
import com.sonyged.hyperClass.model.StudentPage
import com.sonyged.hyperClass.viewmodel.StudentViewModel

class StudentFragment : BaseFragment(R.layout.fragment_student) {

    companion object {

        fun create(): Fragment {
            return StudentFragment()
        }
    }

    private val binding: FragmentStudentBinding by lazy {
        FragmentStudentBinding.bind(requireView())
    }

    private val viewModel: StudentViewModel by lazy {
        ViewModelProvider(requireActivity()).get(StudentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.student.observe(viewLifecycleOwner) { updateStudent(it) }
    }

    private fun updateStudent(student: StudentPage) {
        binding.idPassword.text = getString(R.string.id_password_value, student.loginId, student.password)
        binding.email.text = student.email
        if (student.courses.isNotEmpty()) {
            binding.course.text = student.courses[0]
        }

        student.tag.forEach {
            val chipBinding = ViewChipTagBinding.inflate(LayoutInflater.from(requireContext()))
            chipBinding.root.text = it
            binding.chipGroup.addView(chipBinding.root)

        }

    }
}