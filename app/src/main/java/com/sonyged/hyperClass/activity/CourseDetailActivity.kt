package com.sonyged.hyperClass.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.KEY_COURSE_ID
import com.sonyged.hyperClass.databinding.ActivityCourseDetailBinding
import com.sonyged.hyperClass.model.CourseDetail
import com.sonyged.hyperClass.viewmodel.CourseDetailViewModel
import com.sonyged.hyperClass.viewmodel.CourseDetailViewModelFactory
import timber.log.Timber

class CourseDetailActivity : BaseActivity() {

    private val binding: ActivityCourseDetailBinding by lazy {
        ActivityCourseDetailBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<CourseDetailViewModel> {
        val id = intent.getStringExtra(KEY_COURSE_ID) ?: ""
        CourseDetailViewModelFactory(application, id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        viewModel.courseDetail.observe(this) { updateCourseDetail(it) }
    }

    private fun setupView() {

        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun updateCourseDetail(courseDetail: CourseDetail) {
        Timber.d("updateCourseDetail - courseDetail: $courseDetail")

        binding.title.text = courseDetail.name

        binding.name.text1.setText(R.string.course_name)
        binding.name.text2.text = courseDetail.name
        binding.tag.text1.setText(R.string.tag)
        binding.tag.text2.text = courseDetail.tag
        binding.teacher.text1.setText(R.string.teacher)
        binding.teacher.text2.text = courseDetail.teacher
        binding.date.text1.setText(R.string.end_date)
        binding.date.text2.text = courseDetail.endDate
        binding.autoCreate.text1.setText(R.string.auto_create_workout_end)


    }
}