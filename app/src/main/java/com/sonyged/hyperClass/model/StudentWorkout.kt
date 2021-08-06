package com.sonyged.hyperClass.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudentWorkout(
    val id: String,
    val studentName: String,
    val submittedAt: Long,
    val status: String?,
    val statusResource: StatusResource?,
    val answer: String,
    val comment: String,
    val attachments: List<Attachment>
) : Parcelable {

    fun getIdToLong(): Long {
        return id.hashCode().toLong()
    }
}