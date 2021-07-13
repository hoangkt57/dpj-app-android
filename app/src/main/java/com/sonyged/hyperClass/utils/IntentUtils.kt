package com.sonyged.hyperClass.utils

import android.content.Context
import android.content.Intent
import com.sonyged.hyperClass.activity.ExerciseActivity
import com.sonyged.hyperClass.activity.MainActivity
import com.sonyged.hyperClass.activity.StudentActivity
import com.sonyged.hyperClass.activity.StudentListActivity
import com.sonyged.hyperClass.constants.KEY_COURSE
import com.sonyged.hyperClass.constants.KEY_ID
import com.sonyged.hyperClass.constants.KEY_LESSON
import com.sonyged.hyperClass.constants.KEY_STUDENT_ID
import com.sonyged.hyperClass.model.Course
import timber.log.Timber

private fun startActivityWithException(context: Context, intent: Intent) {
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Timber.e(e, "startActivityWithException")
    }
}

fun startStudentActivity(context: Context, studentId: String) {
    val intent = Intent(context, StudentActivity::class.java)
    intent.putExtra(KEY_STUDENT_ID, studentId)
    startActivityWithException(context, intent)
}

fun startLessonActivity(context: Context, lessonId: String) {
    startExerciseActivity(context, true, lessonId)
}

fun startWorkoutActivity(context: Context, workoutId: String) {
    startExerciseActivity(context, false, workoutId)
}

fun startMainActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    context.startActivity(intent)
}

fun startStudentActivity(context: Context, course: Course) {
    val intent = Intent(context, StudentListActivity::class.java)
    intent.putExtra(KEY_COURSE, course)
    context.startActivity(intent)
}

private fun startExerciseActivity(context: Context, isLesson: Boolean, id: String) {
    val intent = Intent(context, ExerciseActivity::class.java)
    intent.putExtra(KEY_ID, id)
    intent.putExtra(KEY_LESSON, isLesson)
    context.startActivity(intent)
}

