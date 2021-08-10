package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.contract.OpenWorkout
import com.sonyged.hyperClass.model.BaseItem
import com.sonyged.hyperClass.model.ExerciseFilter
import com.sonyged.hyperClass.utils.formatDate1

class CourseWorkoutFragment : BaseExerciseFragment() {

    companion object {

        fun create(): Fragment {
            return CourseWorkoutFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.workouts.observe(viewLifecycleOwner) { updateWorkouts(it) }
        viewModel.workoutFilter.observe(viewLifecycleOwner) { updateDateRange(it) }
    }

    private fun updateWorkouts(workouts: List<BaseItem>) {
        updateData(workouts)
    }

    private fun updateDateRange(filter: ExerciseFilter) {
        val dateRange = filter.dateRange ?: return
        binding.date.text = getString(R.string.lesson_date_range, formatDate1(dateRange.first), formatDate1(dateRange.second))
    }

    override fun onItemClick(position: Int) {
        super.onItemClick(position)

        adapter.getAdapterItem(position)?.let {
            openWorkout.launch(it)
        }

    }

    override fun showMore() {
        viewModel.loadMoreWorkouts()
    }

    private val openWorkout = registerForActivityResult(OpenWorkout()) {}
}