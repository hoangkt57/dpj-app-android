package com.sonyged.hyperClass.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.databinding.ItemStudentBinding
import com.sonyged.hyperClass.model.Student

class StudentViewHolder(private val listener: OnItemClickListener?, private val binding: ItemStudentBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener?.onItemClick(absoluteAdapterPosition)
        }
    }

    fun bindView(student: Student) {

        binding.name.text = student.name

    }


}