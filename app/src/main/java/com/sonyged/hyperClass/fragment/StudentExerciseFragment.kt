package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.ExerciseAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.databinding.FragmentStudentExerciseBinding
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.viewmodel.StudentViewModel
import com.sonyged.hyperClass.views.CourseSpaceItemDecoration
import timber.log.Timber

abstract class StudentExerciseFragment() : BaseFragment(R.layout.fragment_student_exercise), OnItemClickListener {

    protected val binding: FragmentStudentExerciseBinding by lazy {
        FragmentStudentExerciseBinding.bind(requireView())
    }

    protected val viewModel: StudentViewModel by lazy {
        ViewModelProvider(requireActivity()).get(StudentViewModel::class.java)
    }

    protected val adapter: ExerciseAdapter by lazy {
        ExerciseAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loading.show()
        binding.recyclerView.apply {
            addItemDecoration(CourseSpaceItemDecoration(requireContext()))
            adapter = this@StudentExerciseFragment.adapter
        }
    }

    protected fun updateData(data: List<Exercise>) {
        Timber.d("updateData - size: ${data.size}")
        adapter.submitList(data)
        binding.loading.hide()
    }

    override fun onItemClick(position: Int) {

    }


}