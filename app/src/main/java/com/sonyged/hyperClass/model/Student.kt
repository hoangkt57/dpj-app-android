package com.sonyged.hyperClass.model

import androidx.recyclerview.widget.DiffUtil

data class Student(
    val id: String,
    val name: String,
    val icon: Int,
    val type: String
) {

    class DiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }
}