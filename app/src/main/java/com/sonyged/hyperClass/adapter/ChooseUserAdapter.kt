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
    ListAdapter<Student, ChooseUserViewHolder>(Student.DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseUserViewHolder {
        return ChooseUserViewHolder(
            listener,
            isMultipleSelection,
            ItemChooseUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChooseUserViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    fun getAdapterItem(position: Int): Student {
        return getItem(position)
    }
}