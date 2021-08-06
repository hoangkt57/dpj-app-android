package com.sonyged.hyperClass.model

import android.os.Parcelable
import com.sonyged.hyperClass.type.WorkoutStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class Workout(
    val id: String,
    val studentWorkoutId: String?,
    val name: String,
    val description: String,
    val date: Long,
    val status: WorkoutStatus?,
    val files: List<Attachment>,
    val submittedAt: Long,
    val answer: String,
    val submissionFile: List<Attachment>,
    val comment: String
) : Parcelable {

    companion object {
        fun empty(): Workout {
            return Workout(
                "",
                null,
                "",
                "",
                -1,
                WorkoutStatus.UNKNOWN__,
                arrayListOf(),
                -1,
                "",
                arrayListOf(),
                ""
            )
        }
    }

}