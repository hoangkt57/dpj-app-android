package com.sonyged.hyperClass.model

import android.os.Parcelable
import com.sonyged.hyperClass.type.DefaultCourseCoverImageOption
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourseDetail(
    val id: String,
    val name: String,
    val tags: List<Tag>,
    val teacher: Person,
    val assistant: Person,
    val endDate: Long,
    val autoCreateLessonWorkout: Boolean,
    val icon: DefaultCourseCoverImageOption
) : Parcelable
