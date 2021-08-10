package com.sonyged.hyperClass.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.databinding.ItemExerciseBinding
import com.sonyged.hyperClass.databinding.ViewItemExerciseFileBinding
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.type.UserEventFilterType
import com.sonyged.hyperClass.utils.previewFileActivity

class ExerciseViewHolder(private val listener: OnItemClickListener?, private val binding: ItemExerciseBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener?.onItemClick(absoluteAdapterPosition)
        }
        binding.answerLayout.setOnClickListener {
            showAnswer()
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

        if (exercise.status != null) {
            binding.status.visibility = View.VISIBLE
            binding.status.text = exercise.status.text
            binding.status.setTextColor(ContextCompat.getColor(itemView.context, exercise.status.textColor))
            binding.status.setBackgroundColor(ContextCompat.getColor(itemView.context, exercise.status.bgColor))
        } else {
            binding.status.visibility = View.INVISIBLE
        }

        if (exercise.kickUrl == null) {
            binding.start.visibility = View.INVISIBLE
        } else {
            binding.start.visibility = View.VISIBLE
        }

        if (!exercise.answer.isNullOrEmpty()) {
            binding.answerLayout.clipToOutline = true
            binding.answerLayout.isSelected = false
            binding.answerLayout.visibility = View.VISIBLE
            binding.answer.text = exercise.answer
            binding.fileLayout.removeAllViews()
            exercise.attachments?.forEach { attachment ->
                val fileBinding = ViewItemExerciseFileBinding.inflate(LayoutInflater.from(itemView.context))
                fileBinding.root.text = attachment.filename
                fileBinding.root.setOnClickListener {
                    previewFileActivity(itemView.context, attachment)
                }
                binding.fileLayout.addView(fileBinding.root)
            }
        } else {
            binding.answerLayout.visibility = View.GONE
        }
    }

    private fun showAnswer() {
        val isShow = !binding.answerLayout.isSelected
        if (isShow) {
            binding.answer.maxLines = 1000
            binding.down.rotation = 180f
            binding.fileLayout.visibility = View.VISIBLE
        } else {
            binding.answer.maxLines = 1
            binding.down.rotation = 0f
            binding.fileLayout.visibility = View.GONE
        }
        binding.answerLayout.isSelected = isShow
    }
}