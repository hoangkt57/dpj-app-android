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
        val course = arguments?.getParcelable(KEY_COURSE) ?: Course.empty()
        ViewModelProvider(
            requireActivity(),
            CourseViewModelFactory(requireActivity().application, course)
        ).get(CourseViewModel::class.java)
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

        binding.recyclerView.apply {
            addItemDecoration(CourseSpaceItemDecoration(requireContext()))
            adapter = this@BaseExerciseFragment.adapter
        }
    }

    protected fun updateData(data: List<Exercise>) {
        Timber.d("updateData - size: ${data.size}")
        adapter.submitList(data)
    }

    override fun onItemClick(position: Int) {

    }

}