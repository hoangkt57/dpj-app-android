package com.sonyged.hyperClass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sonyged.hyperClass.adapter.viewholder.ChooseUserViewHolder
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.databinding.ItemChooseUserBinding
import com.sonyged.hyperClass.model.Student

class ChooseUserAdapter(
    private val listener: OnItemClickListener?,
    private val isMultipleSelection: Boolean
) :
    ListAdapter<Student, ChooseUserViewHolder>(Student.DiffCallback()), OnItemClickListener {

    private var itemSelected: HashMap<String, Boolean>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseUserViewHolder {
        val l = if (isMultipleSelection) this else listener
        return ChooseUserViewHolder(
            l,
            isMultipleSelection,
            ItemChooseUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChooseUserViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindView(getItem(position), itemSelected?.get(item.id) == true)
    }

    fun getAdapterItem(position: Int): Student {
        return getItem(position)
    }

    fun setItemSelected(itemSelected: HashMap<String, Boolean>) {
        this.itemSelected = itemSelected
    }

    override fun onItemClick(position: Int) {
        val item = getItem(position)
        if (itemSelected?.get(item.id) == true) {
            itemSelected?.remove(item.id)
        } else {
            itemSelected?.put(item.id, true)
        }
        notifyItemChanged(position)
    }
}