package com.sonyged.hyperClass.fragment

import android.content.Intent
import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.activity.ExerciseActivity
import com.sonyged.hyperClass.constants.KEY_ID
import com.sonyged.hyperClass.constants.KEY_LESSON
import com.sonyged.hyperClass.model.Exercise

abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {

    protected fun startLessonActivity(lesson: Exercise) {
        val intent = Intent(requireContext(), ExerciseActivity::class.java)
        intent.putExtra(KEY_ID, lesson.id)
        intent.putExtra(KEY_LESSON, true)
        startActivity(intent)
    }

    protected fun startWorkoutActivity(workout: Exercise) {
        val intent = Intent(requireContext(), ExerciseActivity::class.java)
        intent.putExtra(KEY_ID, workout.id)
        startActivity(intent)
    }
}