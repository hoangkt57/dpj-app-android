package com.sonyged.hyperClass.model

import androidx.recyclerview.widget.DiffUtil
import com.apollographql.apollo.api.EnumValue

data class Student(
    val id: String,
    val name: String,
    val icon: Int,
    val type: String,
    val status: EnumValue?,
    val statusResource: StatusResource?
) {
    constructor(id: String, name: String, icon: Int, type: String) : this(id, name, icon, type, null, null)

    class DiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }
}