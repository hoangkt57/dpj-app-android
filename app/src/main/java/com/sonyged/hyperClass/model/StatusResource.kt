package com.sonyged.hyperClass.model

import com.apollographql.apollo.api.EnumValue
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.type.WorkoutStatus

data class StatusResource(
    val textColor: Int,
    val bgColor: Int,
    val text: Int
) {
    companion object {
        fun getStatus(status: EnumValue): StatusResource {
            return when (status) {
                WorkoutStatus.NONE -> {
                    StatusResource(R.color.on_primary, R.color.workout_none, R.string.not_proposed)
                }
                WorkoutStatus.DRAFT -> {
                    StatusResource(R.color.on_primary, R.color.workout_draft, R.string.draft)
                }
                WorkoutStatus.SUBMITTED -> {
                    StatusResource(R.color.color_secondary, R.color.workout_submitted, R.string.submitted)
                }
                WorkoutStatus.REVIEWED -> {
                    StatusResource(R.color.color_secondary, R.color.workout_reviewed, R.string.reviewed)
                }
                WorkoutStatus.REJECTED -> {
                    StatusResource(R.color.on_primary, R.color.workout_reject, R.string.rejected)
                }
                else -> {
                    StatusResource(0, 0, 0)
                }
            }
        }
    }
}