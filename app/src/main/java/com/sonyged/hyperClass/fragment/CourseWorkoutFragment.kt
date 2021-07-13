package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.model.Exercise
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
    }

    private fun updateWorkouts(workouts: List<Exercise>) {
        updateData(workouts)
    }

    override fun onItemClick(position: Int) {
        super.onItemClick(position)

        val context = context ?: return
        val exercise = adapter.getAdapterItem(position)
        startWorkoutActivity(context, exercise.id)
    }
}