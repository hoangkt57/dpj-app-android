package com.sonyged.hyperClass.model

data class CourseDetail(
    val id: String,
    val name: String,
    val tag: String,
    val teacher: String,
    val endDate: String,
    val autoCreateLessonWorkout: Boolean,
    val icon: Int
)
