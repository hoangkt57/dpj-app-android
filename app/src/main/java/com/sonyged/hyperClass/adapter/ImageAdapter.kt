package com.sonyged.hyperClass.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sonyged.hyperClass.adapter.viewholder.ImageViewHolder
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.databinding.ItemImageBinding
import com.sonyged.hyperClass.model.Attachment

class ImageAdapter(private val listener: OnItemClickListener?) : ListAdapter<Attachment, ImageViewHolder>(Attachment.DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(listener, ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    fun getAdapterItem(position: Int): Attachment {
        return getItem(position)
    }

}