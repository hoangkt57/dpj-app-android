package com.sonyged.hyperClass.model

import androidx.recyclerview.widget.DiffUtil

data class Exercise(
    val id: String,
    val title: String,
    val date: String,
    val type: Int,
    val teacherName: String,
    val courseName : String
) {

    class DiffCallback : DiffUtil.ItemCallback<Exercise>() {
        override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem == newItem
        }
    }


}