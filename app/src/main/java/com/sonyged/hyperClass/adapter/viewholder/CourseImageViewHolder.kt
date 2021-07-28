package com.sonyged.hyperClass.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.databinding.ItemCourseCreateImageBinding
import com.sonyged.hyperClass.glide.GlideApp
import com.sonyged.hyperClass.glide.MyGlideModule
import com.sonyged.hyperClass.type.DefaultCourseCoverImageOption
import com.sonyged.hyperClass.views.getCourseCoverImage

class CourseImageViewHolder(listener: OnItemClickListener?, private val binding: ItemCourseCreateImageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener?.onItemClick(absoluteAdapterPosition)
        }
    }

    fun bindView(data: DefaultCourseCoverImageOption, isChecked: Boolean) {
        GlideApp.with(binding.image)
            .load(getCourseCoverImage(data))
            .apply(MyGlideModule.noCacheOptions())
            .into(binding.image)

        binding.check.visibility = if (isChecked) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}