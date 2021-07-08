package com.sonyged.hyperClass.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.sonyged.hyperClass.constants.KEY_WORKOUT_ID
import com.sonyged.hyperClass.constants.KEY_WORKOUT_NAME
import com.sonyged.hyperClass.databinding.ActivityWorkoutBinding
import com.sonyged.hyperClass.model.Workout
import com.sonyged.hyperClass.viewmodel.WorkoutViewModel
import com.sonyged.hyperClass.viewmodel.WorkoutViewModelFactory
import timber.log.Timber

class WorkoutActivity : BaseActivity() {

    private val binding: ActivityWorkoutBinding by lazy {
        ActivityWorkoutBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<WorkoutViewModel> {
        val workoutId = intent.getStringExtra(KEY_WORKOUT_ID) ?: ""
        WorkoutViewModelFactory(application, workoutId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        viewModel.workout.observe(this) { updateWorkout(it) }
    }

    private fun setupView() {

        binding.back.setOnClickListener {
            finish()
        }

        binding.editButton.setOnClickListener {
            startSubmissionActivity()
        }
    }

    private fun updateWorkout(workout: Workout) {
        Timber.d("updateWorkout - workout: $workout")

        binding.title.text = workout.name
        binding.date.text = workout.date

        binding.course.text = workout.courseName
        binding.term.text = workout.date


    }

    private fun startSubmissionActivity() {
        val name = viewModel.workout.value?.name ?: ""
        val intent = Intent(this, SubmissionActivity::class.java)
        intent.putExtra(KEY_WORKOUT_NAME, name)
        startActivity(intent)
    }
}