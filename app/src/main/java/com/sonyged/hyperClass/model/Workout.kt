package com.sonyged.hyperClass.model

import com.sonyged.hyperClass.PageWorkoutQuery
import com.sonyged.hyperClass.type.WorkoutStatus

data class Workout(
    val id: String,
    val name: String,
    val courseName: String,
    val description: String,
    val date: String,
    val data: String,
    val status: WorkoutStatus,
    val yourAnswer: String,
    val files : List<PageWorkoutQuery.Attachment>
)