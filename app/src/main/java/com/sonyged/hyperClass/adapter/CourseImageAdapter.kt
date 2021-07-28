package com.sonyged.hyperClass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.sonyged.hyperClass.adapter.viewholder.CourseImageViewHolder
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.databinding.ItemCourseCreateImageBinding
import com.sonyged.hyperClass.type.DefaultCourseCoverImageOption

class CourseImageAdapter(private val listener: OnItemClickListener?) :
    ListAdapter<DefaultCourseCoverImageOption, CourseImageViewHolder>(DiffCallback()), OnItemClickListener {

    private var positionSelected = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseImageViewHolder {
        return CourseImageViewHolder(this, ItemCourseCreateImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CourseImageViewHolder, position: Int) {
        holder.bindView(getItem(position), positionSelected == position)
    }

    fun getAdapterItem(position: Int): DefaultCourseCoverImageOption {
        return getItem(position)
    }

    class DiffCallback : DiffUtil.ItemCallback<DefaultCourseCoverImageOption>() {
        override fun areItemsTheSame(oldItem: DefaultCourseCoverImageOption, newItem: DefaultCourseCoverImageOption): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DefaultCourseCoverImageOption, newItem: DefaultCourseCoverImageOption): Boolean {
            return true
        }
    }

    override fun onItemClick(position: Int) {
        val oldPosition = positionSelected
        positionSelected = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(position)
        listener?.onItemClick(position)
    }

    fun setPositionSelected(position: Int) {
        positionSelected = position
        notifyItemChanged(position)
    }

}