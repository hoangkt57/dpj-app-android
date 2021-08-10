package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.ExerciseAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.adapter.viewholder.OnMoreClickListener
import com.sonyged.hyperClass.databinding.FragmentCourseLessonBinding
import com.sonyged.hyperClass.model.BaseItem
import com.sonyged.hyperClass.viewmodel.CourseViewModel
import com.sonyged.hyperClass.views.CourseSpaceItemDecoration
import timber.log.Timber

abstract class BaseExerciseFragment : BaseFragment(R.layout.fragment_course_lesson), OnItemClickListener, OnMoreClickListener {

    protected val viewModel: CourseViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CourseViewModel::class.java)
    }

    protected val binding: FragmentCourseLessonBinding by lazy {
        FragmentCourseLessonBinding.bind(requireView())
    }

    protected val adapter: ExerciseAdapter by lazy {
        ExerciseAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loading.show()
        adapter.setOnItemClickListener(this)
        adapter.setOnMoreClickListener(this)
        binding.recyclerView.apply {
            addItemDecoration(CourseSpaceItemDecoration(requireContext()))
            adapter = this@BaseExerciseFragment.adapter
        }

        binding.date.setOnClickListener {
            dateRangePickerDialog()
        }
    }

    protected fun updateData(data: List<BaseItem>) {
        Timber.d("updateData - size: ${data.size}")
        adapter.submitList(data)
        binding.recyclerView.visibility = View.VISIBLE
        binding.loading.hide()
    }

    override fun onItemClick(position: Int) {

    }

    private fun dateRangePickerDialog() {
        Timber.d("dateRangePickerDialog")
        val dateRangePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
        dateRangePickerBuilder.setTitleText(R.string.picker_range_header_title)
        val dateRangePicker = dateRangePickerBuilder.build()
        dateRangePicker.show(childFragmentManager, "dateRangePicker")
        dateRangePicker.addOnPositiveButtonClickListener { date ->
            Timber.d("dateRangePickerDialog - date: $date")
            binding.loading.show()
            binding.recyclerView.visibility = View.INVISIBLE
            when (this) {
                is CourseLessonFragment -> {
                    viewModel.setLessonDateRange(Pair(date.first, date.second))
                }
                is CourseWorkoutFragment -> {
                    viewModel.setWorkoutDateRange(Pair(date.first, date.second))
                }
                else -> {
                    throw NullPointerException()
                }
            }

        }
    }

}