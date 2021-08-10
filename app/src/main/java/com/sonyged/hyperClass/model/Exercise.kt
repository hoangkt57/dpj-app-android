package com.sonyged.hyperClass.model

import androidx.recyclerview.widget.DiffUtil
import com.sonyged.hyperClass.type.UserEventFilterType

data class Exercise(
    override val id: String,
    val title: String,
    val date: String,
    val type: UserEventFilterType,
    val teacherName: String,
    val courseName: String,
    val status: StatusResource?,
    val kickUrl: String?,
    val answer: String?,
    val attachments: List<Attachment>?
) : BaseItem() {
    constructor(
        id: String,
        title: String,
        date: String,
        type: UserEventFilterType,
        teacherName: String,
        courseName: String,
        status: StatusResource?,
        kickUrl: String?
    ) : this(id, title, date, type, teacherName, courseName, status, kickUrl, null, null)

    class DiffCallback : DiffUtil.ItemCallback<BaseItem>() {
        override fun areItemsTheSame(oldItem: BaseItem, newItem: BaseItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BaseItem, newItem: BaseItem): Boolean {
            if (oldItem is Exercise && newItem is Exercise) {
                return oldItem == newItem
            } else if (oldItem is Page && newItem is Page) {
                return oldItem == newItem
            }
            return false
        }
    }
}