package com.sonyged.hyperClass.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.ChooseTagAdapter
import com.sonyged.hyperClass.adapter.CourseImageAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.ActivityCourseCreateBinding
import com.sonyged.hyperClass.databinding.DialogStudentAdditionBinding
import com.sonyged.hyperClass.databinding.ViewChipTeacherBinding
import com.sonyged.hyperClass.model.CourseDetail
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.Tag
import com.sonyged.hyperClass.utils.formatDate1
import com.sonyged.hyperClass.viewmodel.CourseCreateViewModel
import com.sonyged.hyperClass.views.CourseImageCoverItemDecoration
import timber.log.Timber
import java.util.*

class CourseCreateActivity : BaseActivity(), OnItemClickListener {

    private val binding: ActivityCourseCreateBinding by lazy {
        ActivityCourseCreateBinding.inflate(layoutInflater)
    }

    private val adapter: CourseImageAdapter by lazy {
        CourseImageAdapter(this)
    }

    private val filterAdapter: ChooseTagAdapter by lazy {
        ChooseTagAdapter(null, true)
    }

    private val viewModel by viewModels<CourseCreateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        intent.getParcelableExtra<CourseDetail>(KEY_COURSE_DETAIL)?.let {
            viewModel.setCourseDetail(it)
        }

        intent.getParcelableArrayListExtra<Tag>(KEY_TAG)?.let {
            viewModel.setTags(it)
        }

        setupView()

        viewModel.courseDetail.observe(this) { updateCourseDetail(it) }
        viewModel.filterTags.observe(this) { updateFilterTags(it) }
        viewModel.status.observe(this) { updateStatus(it) }
    }

    private fun setupView() {

        binding.back.setOnClickListener {
            finish()
        }

        binding.date.setOnClickListener {
            startDatePicker(binding.date)
        }

        binding.create.setOnClickListener {
            viewModel.changeCourse(binding.titleEdit.text.toString(), binding.autoCreateWorkout.isChecked)
        }

        binding.recyclerView.apply {
            adapter = this@CourseCreateActivity.adapter
            addItemDecoration(CourseImageCoverItemDecoration(this@CourseCreateActivity))
        }

        adapter.submitList(viewModel.geCoverImageList())

        binding.tagGroup.setOnClickListener {
            addTagDialog()
        }
    }

    private fun updateStatus(status: Status) {
        when (status.id) {
            STATUS_LOADING -> {
                showProgressDialog()
                hideError()
            }
            STATUS_FAILED -> {
                hideProgressDialog()
                val error = status.extras.getString(KEY_ERROR_MSG) ?: ""
                showError(error)
            }
            STATUS_SUCCESSFUL -> {
                hideProgressDialog()
                Toast.makeText(applicationContext, R.string.course_updated, Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun showError(text: String) {
        binding.error.text = text
        binding.error.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.error.text = ""
        binding.error.visibility = View.GONE
    }

    private fun updateCourseDetail(courseDetail: CourseDetail) {
        Timber.d("updateCourseDetail - courseDetail: $courseDetail")

        binding.titleEdit.setText(courseDetail.name)
        binding.tagGroup.removeAllViews()
        courseDetail.tags.forEach { tag ->
            addTagView(tag)
        }
        binding.date.text = formatDate1(courseDetail.endDate)
        binding.date.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        binding.autoCreateWorkout.isChecked = courseDetail.autoCreateLessonWorkout

        for (i in 0 until adapter.itemCount) {
            val item = adapter.getAdapterItem(i)
            if (item == courseDetail.icon) {
                adapter.setPositionSelected(i)
                viewModel.imageSelected = i
                break
            }
        }
    }

    override fun onItemClick(position: Int) {
        viewModel.imageSelected = position
    }

    private fun startDatePicker(button: MaterialButton) {
        Locale.setDefault(Locale.JAPAN)
        resources?.configuration?.setLocale(Locale.JAPAN)
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        if (viewModel.date != -1L) {
            datePickerBuilder.setSelection(viewModel.date)
        }

        val datePicker = datePickerBuilder.build()
        datePicker.show(supportFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener { date ->
            Timber.d("startDatePicker - change date: $date")
            viewModel.date = date
            button.text = formatDate1(date)
            button.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        }
    }

    private fun addTagDialog() {
        val tags = viewModel.getTagsDialog()
        if (tags.isEmpty()) {
            return
        }
        val viewBinding = DialogStudentAdditionBinding.inflate(layoutInflater)
        filterAdapter.clearItemSelected()
        viewBinding.recyclerView.adapter = filterAdapter
        filterAdapter.submitList(tags)

        viewBinding.search.setOnClickListener {
            viewModel.filterTag(viewBinding.nameEdit.text.toString())
        }

        val dialog = MaterialAlertDialogBuilder(this, R.style.AddStudentDialog)
            .setTitle(R.string.add_tag)
            .setView(viewBinding.root)
            .setNegativeButton(R.string.mtrl_picker_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.addition) { dialog, _ ->
                addNewTag()
                filterAdapter.submitList(null)
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun updateFilterTags(filterTags: List<Tag>) {
        filterAdapter.submitList(filterTags)
    }

    private fun addTagView(tag: Tag) {
        val chipBinding = ViewChipTeacherBinding.inflate(layoutInflater)
        chipBinding.root.text = tag.name
        chipBinding.root.setOnCloseIconClickListener { view ->
            binding.tagGroup.removeView(view)
            viewModel.removeTag(tag)
        }
        binding.tagGroup.addView(chipBinding.root)
    }

    private fun addNewTag() {
        val tags = viewModel.addTags(filterAdapter.getItemSelected())
        binding.tagGroup.removeAllViews()
        tags.forEach {
            addTagView(it)
        }
    }


}