package com.sonyged.hyperClass.contract

import com.sonyged.hyperClass.model.Lesson

data class CreateLessonInput(
    val courseId: String = "",
    val lesson: Lesson? = null
)