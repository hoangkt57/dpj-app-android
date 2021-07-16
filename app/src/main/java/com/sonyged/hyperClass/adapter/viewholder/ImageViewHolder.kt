package com.sonyged.hyperClass.adapter.viewholder

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.databinding.ItemImageBinding
import com.sonyged.hyperClass.glide.GlideApp
import com.sonyged.hyperClass.glide.MyGlideModule

class ImageViewHolder(listener: OnItemClickListener?, private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.delete.setOnClickListener {
            listener?.onItemClick(absoluteAdapterPosition)
        }
    }

    fun bindView(uri: Uri) {
        GlideApp.with(binding.image)
            .load(uri)
            .apply(MyGlideModule.noCacheOptions())
            .into(binding.image)
    }
}