package com.sonyged.hyperClass.adapter.viewholder

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.databinding.ItemCourseBinding
import com.sonyged.hyperClass.databinding.ViewChipTagBinding
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.views.getCourseCoverImage

class CourseViewHolder(private val listener: OnItemClickListener?, private val binding: ItemCourseBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener?.onItemClick(absoluteAdapterPosition)
        }
    }

    fun bindView(course: Course) {

        binding.title.text = course.title
        binding.teacher.text = course.teacher.name
        binding.count.text = course.studentCount.toString()

        binding.logo.setImageResource(getCourseCoverImage(course.coverImage))

        binding.tagGroup.removeAllViews()
        course.tags.forEach {
            val chipBinding = ViewChipTagBinding.inflate(LayoutInflater.from(itemView.context))
            chipBinding.root.text = it
            binding.tagGroup.addView(chipBinding.root)
        }
    }


}