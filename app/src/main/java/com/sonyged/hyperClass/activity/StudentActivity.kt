package com.sonyged.hyperClass.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.StudentPageAdapter
import com.sonyged.hyperClass.constants.EVENT_LESSON_CHANGE
import com.sonyged.hyperClass.constants.EVENT_WORKOUT_CHANGE
import com.sonyged.hyperClass.constants.KEY_STUDENT_ID
import com.sonyged.hyperClass.constants.KEY_TITLE
import com.sonyged.hyperClass.databinding.ActivityStudentBinding
import com.sonyged.hyperClass.model.StudentPage
import com.sonyged.hyperClass.observer.AppObserver
import com.sonyged.hyperClass.observer.Observer
import com.sonyged.hyperClass.viewmodel.StudentViewModel
import com.sonyged.hyperClass.viewmodel.StudentViewModelFactory
import timber.log.Timber

class StudentActivity : BaseActivity(), Observer {

    private val binding: ActivityStudentBinding by lazy {
        ActivityStudentBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<StudentViewModel> {
        val id = intent.getStringExtra(KEY_STUDENT_ID) ?: ""
        StudentViewModelFactory(application, id)
    }

    private val adapter: StudentPageAdapter by lazy {
        StudentPageAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        viewModel.student.observe(this) { updateStudent(it) }

        AppObserver.getInstance().addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        AppObserver.getInstance().removeObserver(this)
    }

    private fun setupView() {

        intent.getStringExtra(KEY_TITLE)?.let {
            binding.title.text = it
        }
        binding.back.setOnClickListener {
            finish()
        }

        binding.viewPager.apply {
            adapter = this@StudentActivity.adapter
        }

        binding.tabLayout.post {
            binding.tabLayout.getTabAt(1)?.view?.background = ContextCompat.getDrawable(this, R.drawable.bg_tab_divider)
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.information)
                }
                1 -> {
                    tab.text = getString(R.string.lesson)
                }
                else -> {
                    tab.text = getString(R.string.workout)
                }
            }
        }.attach()
    }

    private fun updateStudent(student: StudentPage) {
        binding.title.text = student.name
        binding.id.text = student.loginId
    }

    override fun onEvent(event: Int, data: Bundle?) {
        Timber.d("onEvent - event: $event")
        when(event) {
            EVENT_LESSON_CHANGE -> {
                viewModel.loadLessons()
            }
            EVENT_WORKOUT_CHANGE -> {
                viewModel.loadWorkouts()
            }
        }
    }


}