package com.sonyged.hyperClass.fragment

import android.content.Intent
import androidx.fragment.app.Fragment
import com.sonyged.hyperClass.activity.LessonActivity
import com.sonyged.hyperClass.activity.WorkoutActivity
import com.sonyged.hyperClass.constants.KEY_LESSON_ID
import com.sonyged.hyperClass.constants.KEY_WORKOUT_ID
import com.sonyged.hyperClass.model.Exercise

abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {

    protected fun startLessonActivity(lesson: Exercise) {
        val intent = Intent(requireContext(), LessonActivity::class.java)
        intent.putExtra(KEY_LESSON_ID, lesson.id)
        startActivity(intent)
    }

    protected fun startWorkoutActivity(lesson: Exercise) {
        val intent = Intent(requireContext(), WorkoutActivity::class.java)
        intent.putExtra(KEY_WORKOUT_ID, lesson.id)
        startActivity(intent)
    }
}