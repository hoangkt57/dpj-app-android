package com.sonyged.hyperClass.adapter.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.databinding.ItemStudentBinding
import com.sonyged.hyperClass.model.Student

class StudentViewHolder(
    private val listener: OnItemClickListener?,
    private val actionListener: OnActionClickListener?,
    private val isTeacher: Boolean,
    private val isOwner: Boolean,
    private val binding: ItemStudentBinding
) : BaseStudentViewHolder(binding.root) {

    init {
        if (isTeacher) {
            itemView.setOnClickListener {
                listener?.onItemClick(absoluteAdapterPosition)
            }
            binding.delete.setOnClickListener {
                actionListener?.onActionClick(absoluteAdapterPosition)
            }
        }
    }

    override fun bindView(student: Student) {
        binding.name.text = student.name
        if (!isOwner) {
            binding.delete.visibility = View.INVISIBLE
        }
        if (isTeacher) {
            binding.name.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.color_primary_variant
                )
            )
        } else {
            binding.name.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.color_text_primary
                )
            )
        }
    }


}