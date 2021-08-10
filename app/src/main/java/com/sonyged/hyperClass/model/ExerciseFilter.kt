package com.sonyged.hyperClass.model

import com.sonyged.hyperClass.type.UserEventFilterType

data class ExerciseFilter(
    val dateRange: Pair<Long, Long>?,
    val type: UserEventFilterType?,
    val page: Page?
) {
    companion object {
        fun empty(): ExerciseFilter {
            return ExerciseFilter(null, null, null)
        }
    }
}