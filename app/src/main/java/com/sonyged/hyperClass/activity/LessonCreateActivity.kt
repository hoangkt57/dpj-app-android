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
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.ActivityLessonCreateBinding
import com.sonyged.hyperClass.databinding.PopupTeacherBinding
import com.sonyged.hyperClass.databinding.ViewChipTeacherBinding
import com.sonyged.hyperClass.model.Lesson
import com.sonyged.hyperClass.model.Person
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.Student
import com.sonyged.hyperClass.observer.AppObserver
import com.sonyged.hyperClass.type.DayOfWeek
import com.sonyged.hyperClass.utils.*
import com.sonyged.hyperClass.viewmodel.LessonCreateViewModel
import com.sonyged.hyperClass.viewmodel.LessonCreateViewModelFactory
import timber.log.Timber

class LessonCreateActivity : BaseActivity(), OnItemClickListener {

    private val binding: ActivityLessonCreateBinding by lazy {
        ActivityLessonCreateBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<LessonCreateViewModel>() {
        val courseId = intent.getStringExtra(KEY_COURSE_ID) ?: ""
        LessonCreateViewModelFactory(application, courseId)
    }

    private val teacherAdapter: ChooseUserAdapter by lazy {
        ChooseUserAdapter(this, false)
    }

    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        intent.getParcelableExtra<Lesson>(KEY_LESSON)?.let {
            viewModel.lesson.postValue(it)
        }

        setupView()

        viewModel.lesson.observe(this) { updateLesson(it) }
        viewModel.teachers.observe(this) { updateTeacher(it) }
        viewModel.status.observe(this) { updateStatus(it) }
    }

    private fun setupView() {

        if (viewModel.isEditing()) {
            binding.repetition.visibility = View.GONE
            binding.expandLayout.visibility = View.GONE
            binding.create.setText(R.string.change)
        }
        binding.frequencyEdit.transformationMethod = null
        binding.repeatTimeEdit.transformationMethod = null
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
            startTimePicker(binding.startTime)
        }
        binding.endTime.setOnClickListener {
            startTimePicker(binding.endTime)
        }
        binding.create.setOnClickListener {
            viewModel.createLesson(
                binding.lessonNameEdit.text.toString(),
                binding.record.isChecked,
                binding.cutCamera.isChecked,
                binding.repetition.isChecked,
                binding.untilRadio.isChecked,
                binding.repeatTimeRadio.isChecked,
                binding.frequencyEdit.text.toString(),
                binding.repeatTimeEdit.text.toString(),
                binding.editRepeatSwitch.isChecked
            )
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

        val arrView =
            arrayOf(binding.sunDay, binding.monDay, binding.tueDay, binding.wedDay, binding.thuDay, binding.friDay, binding.satDay)
        val arrData = arrayOf(DayOfWeek.SUN, DayOfWeek.MON, DayOfWeek.TUE, DayOfWeek.WED, DayOfWeek.THU, DayOfWeek.FRI, DayOfWeek.SAT)

        for (i in arrView.indices) {
            arrView[i].setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!viewModel.days.contains(arrData[i])) {
                        viewModel.days.add(arrData[i])
                    }
                } else {
                    viewModel.days.remove(arrData[i])
                }
            }
        }

        binding.editRepeatLayout.setOnClickListener {
            binding.editRepeatSwitch.toggle()
            editRepeatOnce(binding.editRepeatSwitch.isChecked)
        }

    }

    private fun editRepeatOnce(isChecked: Boolean) {
        if (isChecked) {
            viewModel.name1 = binding.lessonNameEdit.text.toString()
            binding.lessonNameEdit.setText(viewModel.name2)
            binding.date.alpha = 0.5f
        } else {
            viewModel.name2 = binding.lessonNameEdit.text.toString()
            binding.lessonNameEdit.setText(viewModel.name1)
            binding.date.alpha = 1f
        }
        binding.date.isEnabled = !isChecked
        binding.date.isClickable = !isChecked
        binding.date.isFocusable = !isChecked
    }

    private fun updateStatus(status: Status) {
        Timber.d("updateStatus - status: $status")
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
                Toast.makeText(applicationContext, R.string.created, Toast.LENGTH_SHORT).show()
                AppObserver.getInstance().sendEvent(EVENT_LESSON_CHANGE)
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
            viewModel.frequencyType = position
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

        if (lesson.batchId != null) {
            binding.editRepeatLayout.visibility = View.VISIBLE
            viewModel.setName(lesson.name)
        }

        binding.lessonNameEdit.setText(lesson.name)
        binding.date.text = formatDate1(lesson.beginAt)
        binding.date.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        binding.startTime.text = formatTime(lesson.beginAt)
        binding.startTime.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        binding.endTime.text = formatTime(lesson.endAt)
        binding.endTime.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))

        val chipBinding = ViewChipTeacherBinding.inflate(layoutInflater)
        chipBinding.root.text = lesson.teacher.name
        chipBinding.root.setOnCloseIconClickListener {
            binding.teacherGroup.removeView(it)
            viewModel.teacher = null
        }
        binding.teacherGroup.removeAllViews()
        binding.teacherGroup.addView(chipBinding.root)

        lesson.assistant?.let { assistant ->
            val assistantBinding = ViewChipTeacherBinding.inflate(layoutInflater)
            assistantBinding.root.text = assistant.name
            assistantBinding.root.setOnCloseIconClickListener {
                binding.assistantGroup.removeView(it)
                viewModel.assistant = null
            }
            binding.assistantGroup.removeAllViews()
            binding.assistantGroup.addView(assistantBinding.root)
        }

        viewModel.teacher = lesson.teacher
        viewModel.assistant = lesson.assistant
        viewModel.date = onlyDateFromTime(lesson.beginAt)
        viewModel.startTime = onlyTimeFromTime(lesson.beginAt)
        viewModel.endTime = onlyTimeFromTime(lesson.endAt)
    }

    private fun startDatePicker(button: MaterialButton) {
        val isStart = button.id == R.id.date
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        if (viewModel.date != -1L && isStart) {
            datePickerBuilder.setSelection(viewModel.date)
        } else if (!isStart && viewModel.endDate != -1L) {
            datePickerBuilder.setSelection(viewModel.endDate)
        }
        val datePicker = datePickerBuilder.build()
        datePicker.show(supportFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener { date ->
            Timber.d("startDatePicker - change date: $date")
            if (button.id == R.id.date) {
                viewModel.date = date
            } else if (button.id == R.id.until) {
                viewModel.endDate = date
            }
            button.text = formatDate1(date)
            button.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        }
    }

    private fun startTimePicker(button: MaterialButton) {
        val timePickerBuilder = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(R.string.select_time)

        if (button.id == R.id.start_time) {
            timePickerBuilder.setHour(viewModel.startTime?.first ?: 0)
            timePickerBuilder.setMinute(viewModel.startTime?.second ?: 0)
        } else if (button.id == R.id.end_time) {
            timePickerBuilder.setHour(viewModel.endTime?.first ?: 0)
            timePickerBuilder.setMinute(viewModel.endTime?.second ?: 0)
        }

        val timePicker = timePickerBuilder.build()

        timePicker.show(supportFragmentManager, "timePicker")

        timePicker.addOnPositiveButtonClickListener {
            Timber.d("startTimePicker - hour: ${timePicker.hour} - minute: ${timePicker.minute}")
            button.text =
                getString(R.string.value_time, timePicker.hour, timePicker.minute)
            button.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.color_primary_variant
                )
            )
            if (button.id == R.id.start_time) {
                viewModel.startTime = Pair(timePicker.hour, timePicker.minute)
                if (viewModel.endTime == null) {
                    val endTimePair = Pair(timePicker.hour + 1, timePicker.minute)
                    binding.endTime.text =
                        getString(R.string.value_time, endTimePair.first, endTimePair.second)
                    binding.endTime.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.color_primary_variant
                        )
                    )
                    viewModel.endTime = endTimePair
                }
            } else if (button.id == R.id.end_time) {
                viewModel.endTime =
                    Pair(
                        if (timePicker.hour == 0) 23 else timePicker.hour,
                        if (timePicker.minute == 0 && timePicker.hour == 0) 59 else timePicker.minute
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
        val item = teacherAdapter.getAdapterItem(position)

        val anchor = when (popupWindow?.contentView?.tag) {
            binding.teacherGroup.id -> {
                viewModel.teacher = Person(item.id, item.name, TEACHER)
                binding.teacherGroup
            }
            binding.assistantGroup.id -> {
                viewModel.assistant = Person(item.id, item.name, TEACHER)
                binding.assistantGroup
            }
            else -> {
                null
            }
        }

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