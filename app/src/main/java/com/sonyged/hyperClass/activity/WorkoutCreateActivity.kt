package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.databinding.ActivityWorkoutCreateBinding
import com.sonyged.hyperClass.utils.formatDate1
import com.sonyged.hyperClass.viewmodel.WorkoutCreateViewModel
import timber.log.Timber
import java.util.*

class WorkoutCreateActivity : BaseActivity() {

    private val binding: ActivityWorkoutCreateBinding by lazy {
        ActivityWorkoutCreateBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<WorkoutCreateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()

        viewModel.fileName.observe(this) { updateFileName(it) }
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

        binding.removeFile.setOnClickListener {
            viewModel.removeFile()
        }
    }

    private fun updateFileName(file: String) {
        if (file.isEmpty()) {
            binding.removeFile.visibility = View.GONE
            binding.fileName.visibility = View.GONE
            binding.fileName.text = ""
        } else {
            binding.fileName.text = file
            binding.fileName.visibility = View.VISIBLE
            binding.removeFile.visibility = View.VISIBLE
        }
    }

    private fun startDatePicker() {
        Locale.setDefault(Locale.JAPAN)
        resources?.configuration?.setLocale(Locale.JAPAN)
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()

        val datePicker = datePickerBuilder.build()
        datePicker.show(supportFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener { date ->
            Timber.d("startDatePicker - change date: $date")

            binding.date.text = formatDate1(date)
            binding.date.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        }
    }

    private fun startTimePicker() {
        Locale.setDefault(Locale.JAPAN)
        resources?.configuration?.setLocale(Locale.JAPAN)
        val timePickerBuilder = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(R.string.select_time)

        val timePicker = timePickerBuilder.build()

        timePicker.show(supportFragmentManager, "timePicker")

        timePicker.addOnPositiveButtonClickListener {
            Timber.d("startTimePicker - hour: ${timePicker.hour} - minute: ${timePicker.minute}")
            binding.time.text = getString(R.string.value_time, timePicker.hour, timePicker.minute)
            binding.time.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
        }
    }

    private val registerPickFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        Timber.d("registerPickFile - uri: $uri")

        viewModel.setFile(uri)
    }

    private fun pickFile() {
        try {
            registerPickFile.launch("*/*")
        } catch (e: Exception) {
            Timber.e(e, "pickFile")
        }
    }

}