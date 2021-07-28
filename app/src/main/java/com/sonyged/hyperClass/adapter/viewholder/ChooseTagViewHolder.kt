package com.sonyged.hyperClass.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.databinding.ItemChooseUserBinding
import com.sonyged.hyperClass.model.Tag

class ChooseTagViewHolder(
    private val listener: OnItemClickListener?,
    private val isMultipleSelection: Boolean,
    private val binding: ItemChooseUserBinding
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener?.onItemClick(absoluteAdapterPosition)
        }
    }

    fun bindView(tag: Tag, isSelected: Boolean) {
        binding.name.text = tag.name
        if (isMultipleSelection) {
            binding.checkbox.visibility = View.VISIBLE
        } else {
            binding.checkbox.visibility = View.GONE
        }
        binding.checkbox.isChecked = isSelected
    }
}
