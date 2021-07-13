package com.sonyged.hyperClass.adapter.viewholder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.databinding.ItemStudentBinding
import com.sonyged.hyperClass.model.Student

class StudentViewHolder(
    private val listener: OnItemClickListener?,
    private val isTeacher: Boolean,
    private val binding: ItemStudentBinding
) : RecyclerView.ViewHolder(binding.root) {

    init {
        if (isTeacher) {
            itemView.setOnClickListener {
                listener?.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    fun bindView(student: Student) {
        binding.name.text = student.name
        if (isTeacher) {
            binding.name.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_primary_variant))
        } else {
            binding.name.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_text_primary))
        }
    }


}