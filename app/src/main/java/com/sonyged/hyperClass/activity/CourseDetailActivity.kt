package com.sonyged.hyperClass.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.KEY_COURSE
import com.sonyged.hyperClass.constants.KEY_COURSE_ID
import com.sonyged.hyperClass.constants.KEY_TEACHER_ID
import com.sonyged.hyperClass.contract.CreateCourse
import com.sonyged.hyperClass.databinding.ActivityCourseDetailBinding
import com.sonyged.hyperClass.databinding.ViewChipTagBinding
import com.sonyged.hyperClass.model.Course
import com.sonyged.hyperClass.model.CourseDetail
import com.sonyged.hyperClass.type.DefaultCourseCoverImageOption
import com.sonyged.hyperClass.utils.formatDate2
import com.sonyged.hyperClass.viewmodel.CourseDetailViewModel
import com.sonyged.hyperClass.viewmodel.CourseDetailViewModelFactory
import com.sonyged.hyperClass.views.getCourseCoverImage
import timber.log.Timber

class CourseDetailActivity : BaseActivity() {

    private val binding: ActivityCourseDetailBinding by lazy {
        ActivityCourseDetailBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<CourseDetailViewModel> {
        val courseId = intent.getStringExtra(KEY_COURSE_ID) ?: ""
        val teacherId = intent.getStringExtra(KEY_TEACHER_ID) ?: ""
        CourseDetailViewModelFactory(application, courseId, teacherId)
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

        if (viewModel.isOwner()) {
            binding.editButton.visibility = View.VISIBLE
        }

        binding.editButton.setOnClickListener {
            viewModel.courseDetail.value?.let {
                createCourse.launch(Pair(it, viewModel.allTags))
            }
        }
    }

    private fun updateCourseDetail(courseDetail: CourseDetail) {
        Timber.d("updateCourseDetail - courseDetail: $courseDetail")

        binding.title.text = courseDetail.name

        binding.name.text1.setText(R.string.course_name)
        binding.name.text2.text = courseDetail.name
        binding.tag.text1.setText(R.string.tag)
        binding.tag.chipGroup.removeAllViews()
        courseDetail.tags.forEach {
            val chipBinding = ViewChipTagBinding.inflate(layoutInflater)
            chipBinding.root.text = it.name
            binding.tag.chipGroup.addView(chipBinding.root)
        }
        binding.teacher.text1.setText(R.string.teacher)
        binding.teacher.text2.text = if (courseDetail.assistant.name.isEmpty()) {
            courseDetail.teacher.name
        } else {
            courseDetail.teacher.name + ", " + courseDetail.assistant.name
        }
        binding.date.text1.setText(R.string.end_date)
        binding.date.text2.text = if (courseDetail.endDate == 0L) {
            getString(R.string.none)
        } else {
            formatDate2(courseDetail.endDate)
        }
        binding.autoCreate.text1.setText(R.string.auto_create_workout_end)

        binding.logo.setImageResource(getCourseCoverImage(courseDetail.icon))
    }

    private val createCourse = registerForActivityResult(CreateCourse()) { isRefresh ->
        Timber.d("createCourse - isRefresh: $isRefresh")
        if (isRefresh) {
            setResult(Activity.RESULT_OK)
            viewModel.loadCourseDetail()
        }
    }
}