package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.ExerciseAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.constants.KEY_COURSE
import com.sonyged.hyperClass.databinding.FragmentCourseLessonBinding
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.viewmodel.CourseViewModel
import com.sonyged.hyperClass.viewmodel.CourseViewModelFactory
import com.sonyged.hyperClass.views.CourseSpaceItemDecoration
import com.sonyged.hyperClass.views.ExerciseSpaceItemDecoration
import timber.log.Timber

abstract class BaseExerciseFragment : BaseFragment(R.layout.fragment_course_lesson), OnItemClickListener {

    protected val viewModel: CourseViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CourseViewModel::class.java)
    }

    protected val binding: FragmentCourseLessonBinding by lazy {
        FragmentCourseLessonBinding.bind(requireView())
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
            adapter = this@BaseExerciseFragment.adapter
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