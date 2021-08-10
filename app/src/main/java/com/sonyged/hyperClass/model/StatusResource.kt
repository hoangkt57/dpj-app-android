package com.sonyged.hyperClass.model

import android.content.Context
import android.os.Parcelable
import com.apollographql.apollo.api.EnumValue
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.type.LessonStatus
import com.sonyged.hyperClass.type.WorkoutStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class StatusResource(
    val textColor: Int,
    val bgColor: Int,
    val text: String
) : Parcelable {
    companion object {

        fun getStatus(context: Context, status: EnumValue?): StatusResource? {
            return when (status) {
                WorkoutStatus.NONE -> {
                    StatusResource(R.color.on_primary, R.color.workout_none, context.getString(R.string.not_proposed))
                }
                WorkoutStatus.DRAFT -> {
                    StatusResource(R.color.on_primary, R.color.workout_draft, context.getString(R.string.draft))
                }
                WorkoutStatus.SUBMITTED -> {
                    StatusResource(R.color.color_secondary, R.color.workout_submitted, context.getString(R.string.submitted))
                }
                WorkoutStatus.REVIEWED -> {
                    StatusResource(R.color.color_secondary, R.color.workout_reviewed, context.getString(R.string.reviewed))
                }
                WorkoutStatus.REJECTED -> {
                    StatusResource(R.color.on_primary, R.color.workout_reject, context.getString(R.string.reintroduction))
                }
                WorkoutStatus.COMPLETED -> {
                    StatusResource(R.color.on_primary, R.color.workout_completion, context.getString(R.string.completion))
                }
                LessonStatus.IN_PROGRESS -> {
                    StatusResource(R.color.lesson_during_class, R.color.lesson_during_class_bg, context.getString(R.string.during_class))
                }
                TeacherStatus.VERIFY -> {
                    StatusResource(
                        R.color.color_secondary_variant,
                        R.color.workout_verify,
                        context.getString(R.string.pending_verification)
                    )
                }
                else -> {
                    null
                }
            }
        }

        fun getStudentStatus(context: Context, status: EnumValue?): StatusResource? {
            return when (status) {
                WorkoutStatus.NONE -> {
                    StatusResource(R.color.color_text_second, 0, context.getString(R.string.not_proposed))
                }
                WorkoutStatus.DRAFT -> {
                    StatusResource(R.color.color_text_primary, 0, context.getString(R.string.draft))
                }
                WorkoutStatus.SUBMITTED -> {
                    StatusResource(R.color.color_error, 0, context.getString(R.string.please_confirm))
                }
                WorkoutStatus.REVIEWED -> {
                    StatusResource(R.color.color_secondary, 0, context.getString(R.string.submitted))
                }
                WorkoutStatus.REJECTED -> {
                    StatusResource(R.color.color_text_primary, 0, context.getString(R.string.reintroduction))
                }
                else -> {
                    null
                }
            }
        }
    }
}