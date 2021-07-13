package com.sonyged.hyperClass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.adapter.viewholder.StudentViewHolder
import com.sonyged.hyperClass.databinding.ItemStudentBinding
import com.sonyged.hyperClass.model.Student

class StudentAdapter(private val listener: OnItemClickListener?, private val isTeacher: Boolean) :
    ListAdapter<Student, StudentViewHolder>(Student.DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        return StudentViewHolder(listener, isTeacher, ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    fun getAdapterItem(position: Int): Student {
        return getItem(position)
    }
}