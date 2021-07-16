package com.sonyged.hyperClass.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sonyged.hyperClass.activity.*
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.model.Student
import timber.log.Timber

private fun startActivityWithException(context: Context, intent: Intent) {
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Timber.e(e, "startActivityWithException")
    }
}

fun startStudentActivity(context: Context, student: Student) {
    val intent = Intent(context, StudentActivity::class.java)
    intent.putExtra(KEY_STUDENT_ID, student.id)
    intent.putExtra(KEY_TITLE, student.name)
    startActivityWithException(context, intent)
}

fun startLessonActivity(context: Context, exercise: Exercise) {
    startExerciseActivity(context, true, exercise)
}

fun startWorkoutActivity(context: Context, exercise: Exercise) {
    startExerciseActivity(context, false, exercise)
}

fun startMainActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    startActivityWithException(context, intent)
}

fun startStudentActivity(context: Context, course: Course) {
    val intent = Intent(context, StudentListActivity::class.java)
    intent.putExtra(KEY_COURSE, course)
    startActivityWithException(context, intent)
}

fun startLogin(context: Context) {
    val intent = Intent(context, LoginActivity::class.java)
    startActivityWithException(context, intent)
}

fun openWeb(context: Context, url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivityWithException(context, browserIntent)
}

fun changePasswordActivity(context: Context, userId: String) {
    val intent = Intent(context, ChangePasswordActivity::class.java)
    intent.putExtra(KEY_USER_ID, userId)
    startActivityWithException(context, intent)
}

fun changePasswordActivityFirst(context: Context, userId: String) {
    val intent = Intent(context, ChangePasswordActivity::class.java)
    intent.putExtra(KEY_USER_ID, userId)
    intent.putExtra(KEY_CHANGE_PASSWORD_FIRST, true)
    startActivityWithException(context, intent)
}

fun startAgreementPpActivity(context: Context, userId: String, changePassword: Boolean) {
    val intent = Intent(context, AgreementPpActivity::class.java)
    intent.putExtra(KEY_USER_ID, userId)
    intent.putExtra(KEY_CHANGE_PASSWORD_FIRST, changePassword)
    startActivityWithException(context, intent)
}

fun startCourseActivity(context: Context, course: Course) {
    val intent = Intent(context, CourseActivity::class.java)
    intent.putExtra(KEY_COURSE, course)
    startActivityWithException(context, intent)
}

fun startCourseDetailActivity(context: Context, courseId: String) {
    val intent = Intent(context, CourseDetailActivity::class.java)
    intent.putExtra(KEY_COURSE_ID, courseId)
    startActivityWithException(context, intent)
}

fun startLessonCreateActivity(context: Context) {
    val intent = Intent(context, LessonCreateActivity::class.java)
    startActivityWithException(context, intent)
}

fun startWorkoutCreateActivity(context: Context) {
    val intent = Intent(context, WorkoutCreateActivity::class.java)
    startActivityWithException(context, intent)
}

private fun startExerciseActivity(context: Context, isLesson: Boolean, exercise: Exercise) {
    val intent = Intent(context, ExerciseActivity::class.java)
    intent.putExtra(KEY_ID, exercise.id)
    intent.putExtra(KEY_LESSON, isLesson)
    intent.putExtra(KEY_TITLE, exercise.title)
    if (isLesson) {
        intent.putExtra(KEY_DATE, exercise.date)
        intent.putExtra(KEY_TEACHER, exercise.teacherName)
    } else {
        intent.putExtra(KEY_TEACHER, exercise.courseName)
    }
    startActivityWithException(context, intent)
}

