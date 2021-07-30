package com.sonyged.hyperClass.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lesson(
    val id: String,
    val batchId: String?,
    val name: String,
    val courseName: String,
    val teacher: Person,
    val assistant: Person?,
    val beginAt: Long,
    val endAt: Long,
    val studentCount: Int,
    val kickUrl: String?
) : Parcelable {
    companion object {
        fun empty(): Lesson {
            return Lesson("", null, "", "", Person.empty(), null, 0, 0, 0, null)
        }
    }
}