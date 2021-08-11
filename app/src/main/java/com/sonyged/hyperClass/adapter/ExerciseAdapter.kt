package com.sonyged.hyperClass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.adapter.viewholder.ExerciseViewHolder
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.adapter.viewholder.OnMoreClickListener
import com.sonyged.hyperClass.adapter.viewholder.PageViewHolder
import com.sonyged.hyperClass.databinding.ItemExerciseBinding
import com.sonyged.hyperClass.databinding.ItemPageBinding
import com.sonyged.hyperClass.model.BaseItem
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.model.Page

class ExerciseAdapter() :
    ListAdapter<BaseItem, RecyclerView.ViewHolder>(Exercise.DiffCallback()) {

    companion object {
        private const val TYPE_ITEM = 1
        private const val TYPE_PAGE = 2
    }

    private var itemListener: OnItemClickListener? = null
    private var moreListener: OnMoreClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemListener = listener
    }

    fun setOnMoreClickListener(listener: OnMoreClickListener?) {
        moreListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_PAGE) {
            return PageViewHolder(moreListener, ItemPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
        return ExerciseViewHolder(itemListener, ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is ExerciseViewHolder && item is Exercise) {
            holder.bindView(item)
        } else if (holder is PageViewHolder) {
            holder.bindView()
        }
    }

    fun getAdapterItem(position: Int): Exercise? {
        if (position in 0 until itemCount) {
            val item = getItem(position)
            if (item is Exercise) {
                return item
            }
        }
        return null
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is Page) {
            return TYPE_PAGE
        }
        return TYPE_ITEM
    }
}