package com.sonyged.hyperClass.model

import android.os.Parcelable
import com.sonyged.hyperClass.type.WorkoutStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class Workout(
    val id: String,
    val name: String,
    val courseName: String,
    val description: String,
    val date: Long,
    val status: WorkoutStatus,
    val files: List<Attachment>
) : Parcelable {

    companion object {
        fun empty(): Workout {
            return Workout("", "", "", "", -1, WorkoutStatus.UNKNOWN__, arrayListOf())
        }
    }

}