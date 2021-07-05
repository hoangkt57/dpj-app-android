package com.sonyged.hyperClass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sonyged.hyperClass.adapter.viewholder.CourseViewHolder
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.databinding.ItemCourseBinding
import com.sonyged.hyperClass.model.Course

class CourseAdapter(private val listener: OnItemClickListener?) : ListAdapter<Course, CourseViewHolder>(Course.DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(listener, ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    fun getAdapterItem(position: Int) : Course{
        return getItem(position)
    }
}