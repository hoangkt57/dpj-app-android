package com.sonyged.hyperClass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sonyged.hyperClass.adapter.viewholder.ExerciseViewHolder
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.databinding.ItemExerciseBinding
import com.sonyged.hyperClass.model.Exercise

class ExerciseAdapter(private val listener: OnItemClickListener?) : ListAdapter<Exercise, ExerciseViewHolder>(Exercise.DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        return ExerciseViewHolder(listener, ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }
}