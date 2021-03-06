package com.sonyged.hyperClass.adapter.viewholder

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.databinding.ItemImageBinding
import com.sonyged.hyperClass.glide.CustomGlideUrl
import com.sonyged.hyperClass.glide.GlideApp
import com.sonyged.hyperClass.glide.MyGlideModule
import com.sonyged.hyperClass.model.Attachment

class ImageViewHolder(listener: OnItemClickListener?, private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.delete.setOnClickListener {
            listener?.onItemClick(absoluteAdapterPosition)
        }
    }

    fun bindView(attachment: Attachment) {
        if (attachment.path == null) {
            GlideApp.with(binding.image)
                .load(CustomGlideUrl(attachment.url, attachment.id))
                .apply(MyGlideModule.noCacheOptions())
                .into(binding.image)
        } else {
            GlideApp.with(binding.image)
                .load(attachment.url)
                .apply(MyGlideModule.noCacheOptions())
                .into(binding.image)
        }
    }
}