package com.sonyged.hyperClass.adapter.viewholder

import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.TYPE_LESSON
import com.sonyged.hyperClass.constants.TYPE_WORKOUT
import com.sonyged.hyperClass.databinding.ItemExerciseBinding
import com.sonyged.hyperClass.model.Exercise

class ExerciseViewHolder(private val listener: OnItemClickListener?, private val binding: ItemExerciseBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener?.onItemClick(absoluteAdapterPosition)
        }
    }

    fun bindView(exercise: Exercise) {

        binding.title.text = exercise.title
        binding.date.text = exercise.date
        binding.teacher.text = itemView.context.getString(R.string.teacher_info, exercise.teacherName, exercise.courseName)

        if (exercise.type == TYPE_WORKOUT) {
            binding.logo.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.exercise_workout_bg))
            binding.logo.setImageResource(R.drawable.ic_workout)
            ImageViewCompat.setImageTintList(
                binding.logo,
                AppCompatResources.getColorStateList(itemView.context, R.color.exercise_workout_tint)
            )
        } else if (exercise.type == TYPE_LESSON) {
            binding.logo.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.exercise_workout_bg))
            binding.logo.setImageResource(R.drawable.ic_lesson)
            ImageViewCompat.setImageTintList(
                binding.logo,
                AppCompatResources.getColorStateList(itemView.context, R.color.exercise_lesson_tint)
            )
        }

    }


}