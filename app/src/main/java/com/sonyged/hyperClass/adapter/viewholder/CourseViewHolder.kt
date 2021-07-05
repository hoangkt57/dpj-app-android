package com.sonyged.hyperClass.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.databinding.ItemCourseBinding
import com.sonyged.hyperClass.model.Course

class CourseViewHolder(private val listener: OnItemClickListener?, private val binding: ItemCourseBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener?.onItemClick(absoluteAdapterPosition)
        }
    }

    fun bindView(exercise: Course) {

        binding.title.text = exercise.title
        binding.teacher.text = exercise.teacherName
        binding.count.text = exercise.studentCount.toString()

        binding.logo.setImageResource(exercise.coverImage)

    }


}