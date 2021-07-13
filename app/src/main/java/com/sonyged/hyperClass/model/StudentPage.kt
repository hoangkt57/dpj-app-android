package com.sonyged.hyperClass.model

data class StudentPage(
    val id: String,
    val loginId: String,
    val name: String,
    val password: String,
    val email: String,
    val courses: ArrayList<String> = arrayListOf(),
    val tag: ArrayList<String> = arrayListOf()
)