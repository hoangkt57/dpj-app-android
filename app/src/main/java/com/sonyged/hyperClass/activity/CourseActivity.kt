package com.sonyged.hyperClass.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.CoursePageAdapter
import com.sonyged.hyperClass.constants.KEY_COURSE
import com.sonyged.hyperClass.contract.OpenStudentList
import com.sonyged.hyperClass.databinding.ActivityCourseBinding
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.utils.startCourseDetailActivity
import com.sonyged.hyperClass.utils.startLessonCreateActivity
import com.sonyged.hyperClass.utils.startWorkoutCreateActivity
import com.sonyged.hyperClass.viewmodel.CourseViewModel
import com.sonyged.hyperClass.viewmodel.CourseViewModelFactory
import timber.log.Timber

class CourseActivity : BaseActivity() {

    private val binding: ActivityCourseBinding by lazy {
        ActivityCourseBinding.inflate(layoutInflater)
    }

    private val adapter: CoursePageAdapter by lazy {
        CoursePageAdapter(this, viewModel.isTeacher())
    }

    private val viewModel by viewModels<CourseViewModel> {
        val course = intent.getParcelableExtra(KEY_COURSE) ?: Course.empty()
        CourseViewModelFactory(application, course)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {

        binding.title.text = viewModel.course.title
        binding.teacher.text = viewModel.course.teacher.name
        binding.studentCount.text = getString(R.string.student_count, viewModel.course.studentCount)

        binding.review.visibility = if (viewModel.isTeacher()) View.VISIBLE else View.INVISIBLE
        binding.create.visibility = if (viewModel.isTeacher()) View.VISIBLE else View.INVISIBLE

        binding.back.setOnClickListener {
            finish()
        }

        binding.review.setOnClickListener {
            startCourseDetailActivity(this, viewModel.course.id)
        }

        binding.create.setOnClickListener {
            startCreateActivity()
        }

        binding.viewPager.apply {
            adapter = this@CourseActivity.adapter
        }

        binding.tabLayout.post {
            binding.tabLayout.getTabAt(1)?.view?.background = ContextCompat.getDrawable(this, R.drawable.bg_tab_divider)
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.lesson)
                }
                1 -> {
                    tab.text = getString(R.string.data)
                }
                else -> {
                    tab.text = getString(R.string.workout)
                }
            }
        }.attach()

        binding.studentCount.setOnClickListener {
            openStudentList.launch(viewModel.course)
        }

        binding.viewPager.registerOnPageChangeCallback(callBack)
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.viewPager.unregisterOnPageChangeCallback(callBack)
    }

    private fun startCreateActivity() {
        when (binding.viewPager.currentItem) {
            0 -> {
                startLessonCreateActivity(this)
            }
            2 -> {
                startWorkoutCreateActivity(this)
            }
        }
    }

    private val callBack = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            if (position == 1) {
                binding.create.hide()
            } else {
                binding.create.show()
            }
        }
    }

    private val openStudentList =
        registerForActivityResult(OpenStudentList()) { count ->
            Timber.d("openStudentList - count: $count")
            if (count != -1) {
                binding.studentCount.text = getString(R.string.student_count, count)
                setResult(RESULT_OK, Intent())
            }
        }
}