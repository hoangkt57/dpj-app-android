package com.sonyged.hyperClass.model

import androidx.recyclerview.widget.DiffUtil
import com.apollographql.apollo.api.EnumValue
import com.sonyged.hyperClass.type.UserEventFilterType

data class Exercise(
    val id: String,
    val title: String,
    val date: String,
    val type: UserEventFilterType,
    val teacherName: String,
    val courseName: String,
    val status: EnumValue,
    val kickUrl: String?,
    val answer: String?,
    val attachments: List<Attachment>?
) {
    constructor(
        id: String,
        title: String,
        date: String,
        type: UserEventFilterType,
        teacherName: String,
        courseName: String,
        status: EnumValue,
        kickUrl: String?
    ) : this(id, title, date, type, teacherName, courseName, status, kickUrl, null, null)

    class DiffCallback : DiffUtil.ItemCallback<Exercise>() {
        override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem == newItem
        }
    }


}