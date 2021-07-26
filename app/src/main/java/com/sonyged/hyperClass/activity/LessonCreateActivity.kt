package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.core.widget.PopupWindowCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.ChooseUserAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.constants.KEY_LESSON
import com.sonyged.hyperClass.databinding.ActivityLessonCreateBinding
import com.sonyged.hyperClass.databinding.PopupTeacherBinding
import com.sonyged.hyperClass.databinding.ViewChipTeacherBinding
import com.sonyged.hyperClass.model.Lesson
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.utils.formatDate1
import com.sonyged.hyperClass.utils.formatTime
import com.sonyged.hyperClass.utils.hideKeyboard
import com.sonyged.hyperClass.viewmodel.LessonCreateViewModel
import com.sonyged.hyperClass.viewmodel.LessonCreateViewModelFactory
import timber.log.Timber
import java.util.*

class LessonCreateActivity : BaseActivity(), OnItemClickListener {

    private val binding: ActivityLessonCreateBinding by lazy {
        ActivityLessonCreateBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<LessonCreateViewModel>() {
        val lesson = intent.getParcelableExtra(KEY_LESSON) ?: Lesson.empty()
        LessonCreateViewModelFactory(application, lesson)
    }

    private val teacherAdapter: ChooseUserAdapter by lazy {
        ChooseUserAdapter(this, false)
    }

    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        viewModel.lesson.observe(this) { updateLesson(it) }
        viewModel.teachers.observe(this) { updateTeacher(it) }
    }

    private fun setupView() {

        if (viewModel.isEditing()) {
            binding.repetition.visibility = View.GONE
            binding.expandLayout.visibility = View.GONE
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.teacherGroup.setOnClickListener {
            showPopup(binding.teacherGroup)
        }

        binding.assistantGroup.setOnClickListener {
            showPopup(binding.assistantGroup)
        }

        binding.date.setOnClickListener {
            startDatePicker(binding.date)
        }

        binding.startTime.setOnClickListener {
            startTimePicker(true)
        }

        binding.endTime.setOnClickListener {
            startTimePicker(false)
        }

        binding.create.setOnClickListener {
            Toast.makeText(this, "Feature is not implemented", Toast.LENGTH_SHORT).show()
        }

        binding.record.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cutCamera.visibility = View.VISIBLE
            } else {
                binding.cutCamera.visibility = View.GONE
            }
        }

        binding.repetition.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.expandLayout.visibility = View.VISIBLE
            } else {
                binding.expandLayout.visibility = View.GONE
            }
        }

        binding.frequencyType.setOnClickListener {
            showFrequencyPopup()
        }

        binding.untilLayout.clipToOutline = true
        binding.repeatTimeLayout.clipToOutline = true

        binding.untilRadio.isChecked = true
        binding.repeatTimeLayout.isEnabled = false

        binding.untilRadio.setOnCheckedChangeListener { _, isChecked ->
            enableView(isChecked)
        }
        binding.repeatTimeRadio.setOnCheckedChangeListener { _, isChecked ->
            enableView(!isChecked)
        }

        binding.until.setOnClickListener {
            startDatePicker(binding.until)
        }

    }

    private fun enableView(isUntil: Boolean) {
        binding.untilRadio.isChecked = isUntil
        binding.repeatTimeRadio.isChecked = !isUntil
        binding.repeatTimeEdit.hideKeyboard()
        binding.repeatTimeEdit.clearFocus()
    }

    private fun showFrequencyPopup() {
        val listPopupWindow = ListPopupWindow(this)
        listPopupWindow.isModal = true
        listPopupWindow.anchorView = binding.frequencyType
        val arr = arrayOf(getString(R.string.each_day), getString(R.string.weekly))
        listPopupWindow.setAdapter(
            ArrayAdapter(
                this,
                R.layout.item_choose_user,
                R.id.name,
                arr
            )
        )
        listPopupWindow.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    binding.frequencyType.text = arr[0]
                    chooseDayOfWeek(false)
                }
                1 -> {
                    binding.frequencyType.text = arr[1]
                    chooseDayOfWeek(true)
                }
            }
            listPopupWindow.dismiss()
        }
        listPopupWindow.show()
    }

    private fun chooseDayOfWeek(isShowing: Boolean) {
        val visibility = if (isShowing) View.VISIBLE else View.GONE
        binding.dayOfWeekText.visibility = visibility
        binding.dayWeekLayout.visibility = visibility
    }

    private fun updateLesson(lesson: Lesson) {

        binding.lessonNameEdit.setText(lesson.name)
        binding.date.text = formatDate1(lesson.beginAt)
        binding.date.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        binding.startTime.text = formatTime(lesson.beginAt)
        binding.startTime.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        binding.endTime.text = formatTime(lesson.endAt)
        binding.endTime.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))

        val chipBinding = ViewChipTeacherBinding.inflate(layoutInflater)
        chipBinding.root.text = lesson.teacher
        chipBinding.root.setOnCloseIconClickListener {
            binding.teacherGroup.removeView(it)
        }
        binding.teacherGroup.addView(chipBinding.root)


    }

    private fun startDatePicker(button: MaterialButton) {
        Locale.setDefault(Locale.JAPAN)
        resources?.configuration?.setLocale(Locale.JAPAN)
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()

        val datePicker = datePickerBuilder.build()
        datePicker.show(supportFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener { date ->
            Timber.d("startDatePicker - change date: $date")

            button.text = formatDate1(date)
            button.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        }
    }

    private fun startTimePicker(isStart: Boolean) {
        Locale.setDefault(Locale.JAPAN)
        resources?.configuration?.setLocale(Locale.JAPAN)
        val timePickerBuilder = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(R.string.select_time)

        val timePicker = timePickerBuilder.build()

        timePicker.show(supportFragmentManager, "timePicker")

        timePicker.addOnPositiveButtonClickListener {
            Timber.d("startTimePicker - hour: ${timePicker.hour} - minute: ${timePicker.minute}")
            if (isStart) {
                binding.startTime.text =
                    getString(R.string.value_time, timePicker.hour, timePicker.minute)
                binding.startTime.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.color_primary_variant
                    )
                )
            } else {
                binding.endTime.text =
                    getString(R.string.value_time, timePicker.hour, timePicker.minute)
                binding.endTime.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.color_primary_variant
                    )
                )
            }
        }
    }

    private fun showPopup(anchor: ChipGroup) {

        val position = IntArray(2)
        anchor.getLocationOnScreen(position)
        val height = binding.root.height - position[1] - anchor.height - 100
        val viewBinding = PopupTeacherBinding.inflate(layoutInflater)
        popupWindow = PopupWindow(viewBinding.root, anchor.width, height)
        popupWindow?.contentView?.tag = anchor.id
        popupWindow?.isOutsideTouchable = true
        popupWindow?.isFocusable = true
        popupWindow?.elevation = 30f
        popupWindow?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_popup))

        viewBinding.recyclerView.adapter = teacherAdapter
        teacherAdapter.submitList(viewModel.teacherList)

        viewBinding.search.setOnClickListener {
            viewModel.filterTeacher(viewBinding.nameEdit.text.toString())
        }

        PopupWindowCompat.showAsDropDown(popupWindow!!, anchor, 0, 30, Gravity.BOTTOM)
    }

    private fun updateTeacher(teachers: List<Student>) {
        teacherAdapter.submitList(teachers)
    }

    override fun onItemClick(position: Int) {
        Timber.d("onItemClick - position: $position")
        val anchor = when (popupWindow?.contentView?.tag) {
            binding.teacherGroup.id -> {
                binding.teacherGroup
            }
            binding.assistantGroup.id -> {
                binding.assistantGroup
            }
            else -> {
                null
            }
        }
        val item = teacherAdapter.getAdapterItem(position)

        val chipBinding = ViewChipTeacherBinding.inflate(layoutInflater)
        chipBinding.root.text = item.name
        chipBinding.root.setOnCloseIconClickListener {
            anchor?.removeView(it)
        }
        anchor?.removeAllViews()
        anchor?.addView(chipBinding.root)

        popupWindow?.dismiss()
        popupWindow = null

    }

}