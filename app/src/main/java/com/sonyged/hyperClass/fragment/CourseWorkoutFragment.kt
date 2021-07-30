package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.utils.formatDate1
import com.sonyged.hyperClass.utils.startWorkoutActivity

class CourseWorkoutFragment : BaseExerciseFragment() {

    companion object {

        fun create(): Fragment {
            return CourseWorkoutFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.workouts.observe(viewLifecycleOwner) { updateWorkouts(it) }
        viewModel.workoutDateRange.observe(viewLifecycleOwner) { updateDateRange(it) }
    }

    private fun updateWorkouts(workouts: List<Exercise>) {
        updateData(workouts)
    }

    private fun updateDateRange(dateRange: Pair<Long, Long>) {
        binding.date.text = getString(R.string.lesson_date_range, formatDate1(dateRange.first), formatDate1(dateRange.second))
    }

    override fun onItemClick(position: Int) {
        super.onItemClick(position)

        val context = context ?: return
        val exercise = adapter.getAdapterItem(position)
        startWorkoutActivity(context, exercise)
    }
}