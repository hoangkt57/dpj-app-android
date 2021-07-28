package com.sonyged.hyperClass.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.sonyged.hyperClass.type.DefaultCourseCoverImageOption
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val id: String,
    val title: String,
    val coverImage: DefaultCourseCoverImageOption,
    val teacher: Person,
    val assistant: Person,
    val studentCount: Int,
    val tags: List<String>
) : Parcelable {

    class DiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        fun empty(): Course {
            return Course(
                "",
                "",
                DefaultCourseCoverImageOption.UNKNOWN__,
                Person.empty(),
                Person.empty(),
                0,
                arrayListOf()
            )
        }
    }

}