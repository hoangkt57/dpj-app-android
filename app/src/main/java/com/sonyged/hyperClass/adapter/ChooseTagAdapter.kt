package com.sonyged.hyperClass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sonyged.hyperClass.adapter.viewholder.ChooseTagViewHolder
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.databinding.ItemChooseUserBinding
import com.sonyged.hyperClass.model.Tag

class ChooseTagAdapter(
    private val listener: OnItemClickListener?,
    private val isMultipleSelection: Boolean
) :
    ListAdapter<Tag, ChooseTagViewHolder>(Tag.DiffCallback()), OnItemClickListener {

    private val itemSelected = hashMapOf<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseTagViewHolder {
        val l = if (isMultipleSelection) this else listener
        return ChooseTagViewHolder(
            l,
            isMultipleSelection,
            ItemChooseUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChooseTagViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindView(getItem(position), itemSelected.get(item.id) == true)
    }

    fun getAdapterItem(position: Int): Tag {
        return getItem(position)
    }

    fun clearItemSelected() {
        itemSelected.clear()
    }

    fun getItemSelected() : HashMap<String, Boolean> {
        return itemSelected
    }

    override fun onItemClick(position: Int) {
        val item = getItem(position)
        if (itemSelected.get(item.id) == true) {
            itemSelected.remove(item.id)
        } else {
            itemSelected.put(item.id, true)
        }
        notifyItemChanged(position)
    }
}