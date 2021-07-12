package com.sonyged.hyperClass.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.CoursePageAdapter
import com.sonyged.hyperClass.constants.KEY_COURSE
import com.sonyged.hyperClass.databinding.ActivityCourseBinding
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.viewmodel.CourseViewModel
import com.sonyged.hyperClass.viewmodel.CourseViewModelFactory

class CourseActivity : BaseActivity() {

    private val binding: ActivityCourseBinding by lazy {
        ActivityCourseBinding.inflate(layoutInflater)
    }

    private val adapter: CoursePageAdapter by lazy {
        CoursePageAdapter(this, viewModel.course, viewModel.isTeacher())
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
        binding.teacher.text = viewModel.course.teacherName
        binding.studentCount.text = getString(R.string.student_count, viewModel.course.studentCount)

        binding.review.visibility = if (viewModel.isTeacher()) View.VISIBLE else View.INVISIBLE

        binding.back.setOnClickListener {
            finish()
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
            startStudentActivity()
        }


    }

    private fun startStudentActivity() {
        val intent = Intent(this, StudentListActivity::class.java)
        intent.putExtra(KEY_COURSE, viewModel.course)
        startActivity(intent)
    }


}