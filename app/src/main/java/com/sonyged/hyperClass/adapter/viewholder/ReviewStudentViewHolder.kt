package com.sonyged.hyperClass.adapter.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import com.sonyged.hyperClass.databinding.ItemReviewStudentBinding
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.type.WorkoutStatus

class ReviewStudentViewHolder(
    private val listener: OnItemClickListener?,
    private val actionListener: OnActionClickListener?,
    private val isOwner: Boolean,
    private val binding: ItemReviewStudentBinding
) : BaseStudentViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener?.onItemClick(absoluteAdapterPosition)
        }
        binding.view.setOnClickListener {
            actionListener?.onActionClick(absoluteAdapterPosition)
        }
    }

    override fun bindView(student: Student) {
        binding.name.text = student.name

        if (student.statusResource != null) {
            binding.status.text = student.statusResource.text
            binding.status.setTextColor(ContextCompat.getColor(itemView.context, student.statusResource.textColor))
        }
        if (student.status != null && student.status != WorkoutStatus.NONE) {
            binding.view.visibility = View.VISIBLE
        } else {
            binding.view.visibility = View.GONE
        }
        if (student.status == WorkoutStatus.REVIEWED) {
            binding.check.visibility = View.VISIBLE
        } else {
            binding.check.visibility = View.GONE
        }
    }


}