package com.sonyged.hyperClass.contract

import com.sonyged.hyperClass.model.Workout

data class CreateWorkoutInput(
    val courseId: String = "",
    val workout: Workout? = null
)