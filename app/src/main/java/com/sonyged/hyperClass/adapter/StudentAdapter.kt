package com.sonyged.hyperClass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sonyged.hyperClass.adapter.viewholder.*
import com.sonyged.hyperClass.databinding.ItemReviewStudentBinding
import com.sonyged.hyperClass.databinding.ItemStudentBinding
import com.sonyged.hyperClass.model.Student

class StudentAdapter(
    private val listener: OnItemClickListener?,
    private val actionListener: OnActionClickListener?,
    private val isTeacher: Boolean,
    private val isOwner: Boolean,
    private val isReview: Boolean
) :
    ListAdapter<Student, BaseStudentViewHolder>(Student.DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseStudentViewHolder {
        return if (isReview && isTeacher) {
            ReviewStudentViewHolder(
                listener,
                actionListener,
                isOwner,
                ItemReviewStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            StudentViewHolder(
                listener,
                actionListener,
                isTeacher,
                isOwner,
                ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: BaseStudentViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    fun getAdapterItem(position: Int): Student {
        return getItem(position)
    }
}