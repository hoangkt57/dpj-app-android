package com.sonyged.hyperClass.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.databinding.ItemPageBinding

class PageViewHolder(private val listener: OnMoreClickListener?, private val binding: ItemPageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.more.setOnClickListener {
            listener?.showMore()
        }
    }
}