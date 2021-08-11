package com.sonyged.hyperClass.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.databinding.ItemPageBinding

class PageViewHolder(private val listener: OnMoreClickListener?, private val binding: ItemPageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.more.setOnClickListener {
            binding.more.visibility = View.INVISIBLE
            binding.progress.visibility = View.VISIBLE
            listener?.showMore()
        }
    }

    fun bindView() {
        binding.more.visibility = View.VISIBLE
        binding.progress.visibility = View.INVISIBLE
    }
}