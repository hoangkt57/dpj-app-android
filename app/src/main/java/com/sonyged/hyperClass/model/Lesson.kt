package com.sonyged.hyperClass.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lesson(
    val id: String,
    val name: String,
    val courseName: String,
    val teacher: String,
    val beginAt: Long,
    val endAt: Long,
    val studentCount: Int,
    val kickUrl: String?
) : Parcelable {
    companion object {
        fun empty(): Lesson {
            return Lesson("", "", "", "", 0, 0, 0, null)
        }
    }
}