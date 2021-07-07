package com.sonyged.hyperClass.model

data class Lesson(
    val id: String,
    val name: String,
    val courseName: String,
    val date: String,
    val teacher: String,
    val studentCount: Int,
    val kickUrl: String?
)