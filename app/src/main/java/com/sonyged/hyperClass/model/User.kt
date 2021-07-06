package com.sonyged.hyperClass.model

data class User(
    val id: String,
    val loginId: String,
    val name: String,
    val password: String,
    val email: String,
    val isTeacher: Boolean
)