package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.contract.OpenWorkout
import com.sonyged.hyperClass.model.Exercise

class StudentWorkoutFragment : StudentExerciseFragment() {

    companion object {

        fun create(): Fragment {
            return StudentWorkoutFragment()
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

        val exercise = adapter.getAdapterItem(position)
        openWorkout.launch(exercise)
    }

    private val openWorkout = registerForActivityResult(OpenWorkout()) {}
}