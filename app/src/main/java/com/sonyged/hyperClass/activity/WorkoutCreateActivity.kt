package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.ActivityWorkoutCreateBinding
import com.sonyged.hyperClass.databinding.LayoutWorkoutFileBinding
import com.sonyged.hyperClass.model.Attachment
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.Workout
import com.sonyged.hyperClass.observer.AppObserver
import com.sonyged.hyperClass.utils.formatDate1
import com.sonyged.hyperClass.utils.formatTime
import com.sonyged.hyperClass.utils.onlyTimeFromTime
import com.sonyged.hyperClass.viewmodel.WorkoutCreateViewModel
import com.sonyged.hyperClass.viewmodel.WorkoutCreateViewModelFactory
import timber.log.Timber
import java.util.*

class WorkoutCreateActivity : BaseActivity() {

    private val binding: ActivityWorkoutCreateBinding by lazy {
        ActivityWorkoutCreateBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<WorkoutCreateViewModel>() {
        val courseId = intent.getStringExtra(KEY_COURSE_ID) ?: ""
        WorkoutCreateViewModelFactory(application, courseId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        intent.getParcelableExtra<Workout>(KEY_WORKOUT)?.let {
            viewModel.workout.postValue(it)
        }

        setupView()

        viewModel.uris.observe(this) { updateFile(it) }
        viewModel.workout.observe(this) { updateWorkout(it) }
        viewModel.status.observe(this) { updateStatus(it) }
    }

    private fun setupView() {
        binding.back.setOnClickListener {
            finish()
        }

        binding.date.setOnClickListener {
            startDatePicker()
        }

        binding.time.setOnClickListener {
            startTimePicker()
        }

        binding.file.setOnClickListener {
            pickFile()
        }

        binding.create.setOnClickListener {
            viewModel.createWorkout(
                binding.titleEdit.text.toString(),
                binding.contentEdit.text.toString()
            )
        }

        if (viewModel.isEditing()) {
            binding.create.setText(R.string.change)
        }
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
                AppObserver.getInstance().sendEvent(EVENT_WORKOUT_CHANGE)
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

    private fun updateWorkout(workout: Workout) {

        binding.titleEdit.setText(workout.name)
        binding.contentEdit.setText(workout.description)
        binding.date.text = formatDate1(workout.date)
        binding.date.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        binding.time.text = formatTime(workout.date)
        binding.time.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))

        workout.files.forEach { attachment ->
            addFileToLayout(attachment)
        }

        if (workout.date != DATE_INVALID) {
            viewModel.date = workout.date
            viewModel.time = onlyTimeFromTime(workout.date)
        }
    }

    private fun removeFile(id: String) {
        Timber.d("removeFile - id: $id")
        viewModel.removeFile(id)
    }

    private fun updateFile(uris: HashMap<String, Attachment>) {
        binding.fileLayout.removeAllViews()
        viewModel.workout.value?.files?.forEach {
            if (!viewModel.attachmentDeleted.contains(it.id)) {
                addFileToLayout(it)
            }
        }

        uris.forEach {
            addFileToLayout(it.value)
        }
    }

    private fun addFileToLayout(attachment: Attachment) {
        val fileBinding = LayoutWorkoutFileBinding.inflate(layoutInflater)
        fileBinding.name.text = attachment.filename
        fileBinding.remove.setOnClickListener {
            removeFile(attachment.id)
        }
        binding.fileLayout.addView(fileBinding.root)
    }

    private fun startDatePicker() {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        if (viewModel.isEditing()) {
            val date = if (viewModel.date == DATE_INVALID) {
                val workout = viewModel.workout.value
                if (workout != null && workout.date != DATE_INVALID) {
                    workout.date
                } else {
                    System.currentTimeMillis()
                }
            } else {
                viewModel.date
            }
            datePickerBuilder.setSelection(date)
        } else if (viewModel.date != DATE_INVALID) {
            datePickerBuilder.setSelection(viewModel.date)
        }

        val datePicker = datePickerBuilder.build()
        datePicker.show(supportFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener { date ->
            Timber.d("startDatePicker - change date: $date")
            viewModel.date = date
            binding.date.text = formatDate1(date)
            binding.date.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        }
    }

    private fun startTimePicker() {
        val timePickerBuilder = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(R.string.select_time)
        val pair = if (viewModel.isEditing()) {
            if (viewModel.time == null) {
                val workout = viewModel.workout.value
                if (workout != null && workout.date != DATE_INVALID) {
                    onlyTimeFromTime(workout.date)
                } else {
                    null
                }
            } else {
                viewModel.time
            }
        } else {
            viewModel.time
        }
        pair?.let {
            timePickerBuilder.setHour(it.first)
            timePickerBuilder.setMinute(it.second)
        }
        val timePicker = timePickerBuilder.build()
        timePicker.show(supportFragmentManager, "timePicker")
        timePicker.addOnPositiveButtonClickListener {
            Timber.d("startTimePicker - hour: ${timePicker.hour} - minute: ${timePicker.minute}")
            viewModel.time = Pair(timePicker.hour, timePicker.minute)
            binding.time.text = getString(R.string.value_time, timePicker.hour, timePicker.minute)
            binding.time.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        }
    }

    private val registerPickFile =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            Timber.d("registerPickFile - uris: $uris")
            if (uris.size == 0) {
                return@registerForActivityResult
            }
            uris?.let {
                viewModel.setFile(uris)
            }
        }

    private fun pickFile() {
        try {
            registerPickFile.launch("*/*")
        } catch (e: Exception) {
            Timber.e(e, "pickFile")
        }
    }

}