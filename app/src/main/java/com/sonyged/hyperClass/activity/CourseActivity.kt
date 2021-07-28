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
import com.sonyged.hyperClass.contract.OpenCourseDetail
import com.sonyged.hyperClass.contract.OpenStudentList
import com.sonyged.hyperClass.databinding.ActivityCourseBinding
import com.sonyged.hyperClass.model.Course
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
        CourseViewModelFactory(application, course.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        viewModel.course.observe(this) { updateCourse(it) }
    }

    private fun setupView() {

        binding.review.visibility = if (viewModel.isTeacher()) View.VISIBLE else View.INVISIBLE
        binding.create.visibility = if (viewModel.isTeacher()) View.VISIBLE else View.INVISIBLE

        binding.back.setOnClickListener {
            finish()
        }

        binding.review.setOnClickListener {
            viewModel.course.value?.let {
                openCourseDetail.launch(it)
            }
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
            viewModel.course.value?.let {
                openStudentList.launch(it)
            }
        }

        binding.viewPager.registerOnPageChangeCallback(callBack)
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.viewPager.unregisterOnPageChangeCallback(callBack)
    }

    private fun updateCourse(course: Course) {
        binding.title.text = course.title
        binding.teacher.text = course.teacher.name
        binding.studentCount.text = getString(R.string.student_count, course.studentCount)
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
                viewModel.loadCourse()
                setResult(RESULT_OK, Intent())
            }
        }

    private val openCourseDetail =
        registerForActivityResult(OpenCourseDetail()) { isRefresh ->
            Timber.d("openCourseDetail - isRefresh: $isRefresh")
            if (isRefresh) {
                viewModel.loadCourse()
                setResult(RESULT_OK, Intent())
            }
        }


}