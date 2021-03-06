package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.ImageViewCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.ExercisePageAdapter
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.ActivityExerciseBinding
import com.sonyged.hyperClass.observer.AppObserver
import com.sonyged.hyperClass.observer.Observer
import com.sonyged.hyperClass.viewmodel.ExerciseViewModel
import com.sonyged.hyperClass.viewmodel.ExerciseViewModelFactory
import timber.log.Timber

class ExerciseActivity : BaseActivity(), Observer {

    private val binding: ActivityExerciseBinding by lazy {
        ActivityExerciseBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<ExerciseViewModel> {
        val isLesson = intent.getBooleanExtra(KEY_LESSON, false)
        val id = intent.getStringExtra(KEY_ID) ?: ""
        ExerciseViewModelFactory(application, isLesson, id)
    }

    private val adapter: ExercisePageAdapter by lazy {
        ExercisePageAdapter(this, viewModel.isLesson, viewModel.isTeacher())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        viewModel.info.observe(this) { updateInfo(it) }

        AppObserver.getInstance().addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        AppObserver.getInstance().removeObserver(this)
    }

    private fun setupView() {
        if (viewModel.isTeacher()) {
            binding.tabLayout.visibility = View.VISIBLE
        }

        if (viewModel.isLesson) {
            binding.logo.setImageResource(R.drawable.ic_lesson)
            ImageViewCompat.setImageTintList(
                binding.logo,
                AppCompatResources.getColorStateList(this, R.color.exercise_lesson_tint)
            )
        } else {
            binding.logo.setImageResource(R.drawable.ic_workout)
            ImageViewCompat.setImageTintList(
                binding.logo,
                AppCompatResources.getColorStateList(this, R.color.exercise_workout_tint)
            )
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.viewPager.apply {
            adapter = this@ExerciseActivity.adapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.present_situation)
                }
                else -> {
                    if (viewModel.isLesson) {
                        tab.text = getString(R.string.content)
                    } else {
                        tab.text = getString(R.string.description)
                    }
                }
            }
        }.attach()
    }

    private fun updateInfo(info: Triple<String, String, String>) {
        binding.title.text = info.first
        binding.date.text = info.second
        binding.teacher.text = info.third
    }

    override fun onEvent(event: Int, data: Bundle?) {
        Timber.d("onEvent - event: $event")
        when(event) {
            EVENT_LESSON_CHANGE -> {
                viewModel.loadLesson()
            }
            EVENT_WORKOUT_CHANGE -> {
                viewModel.loadWorkout()
            }
            EVENT_REVIEW_CHANGE -> {
                viewModel.loadWorkout()
            }
        }
    }
}