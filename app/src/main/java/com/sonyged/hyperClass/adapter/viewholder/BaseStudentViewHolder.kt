package com.sonyged.hyperClass.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.model.Student

abstract class BaseStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindView(student: Student)
}