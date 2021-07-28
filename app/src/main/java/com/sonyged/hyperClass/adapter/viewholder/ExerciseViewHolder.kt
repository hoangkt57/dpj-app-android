package com.sonyged.hyperClass.adapter.viewholder

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.api.EnumValue
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.databinding.ItemExerciseBinding
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.type.UserEventFilterType
import com.sonyged.hyperClass.type.WorkoutStatus

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

        if (exercise.type == UserEventFilterType.WORKOUT) {
            binding.logo.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.exercise_workout_bg))
            binding.logo.setImageResource(R.drawable.ic_workout)
            ImageViewCompat.setImageTintList(
                binding.logo,
                AppCompatResources.getColorStateList(itemView.context, R.color.exercise_workout_tint)
            )
        } else if (exercise.type == UserEventFilterType.LESSON) {
            binding.logo.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.exercise_workout_bg))
            binding.logo.setImageResource(R.drawable.ic_lesson)
            ImageViewCompat.setImageTintList(
                binding.logo,
                AppCompatResources.getColorStateList(itemView.context, R.color.exercise_lesson_tint)
            )
        }

        val statusValues = getStatus(exercise.status)
        if (statusValues.first != 0) {
            binding.status.visibility = View.VISIBLE
            binding.status.setText(statusValues.first)
            binding.status.setBackgroundColor(ContextCompat.getColor(itemView.context, statusValues.second))
        } else {
            binding.status.visibility = View.INVISIBLE
        }

        if (exercise.kickUrl == null) {
            binding.start.visibility = View.INVISIBLE
        } else {
            binding.start.visibility = View.VISIBLE
        }

    }

    private fun getStatus(status: EnumValue): Pair<Int, Int> {
        return when (status) {
            WorkoutStatus.NONE -> {
                Pair(R.string.not_submitted, R.color.workout_muted)
            }
            WorkoutStatus.DRAFT -> {
                Pair(R.string.draft, R.color.workout_muted)
            }
            WorkoutStatus.SUBMITTED -> {
                Pair(R.string.submitted, R.color.workout_danger)
            }
            WorkoutStatus.REVIEWED -> {
                Pair(R.string.reviewed, R.color.color_primary)
            }
            WorkoutStatus.REJECTED -> {
                Pair(R.string.rejected, R.color.color_secondary)
            }
            else -> {
                Pair(0, 0)
            }
        }
    }


}