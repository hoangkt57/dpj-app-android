package com.sonyged.hyperClass.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.databinding.ActivityLessonCreateBinding
import com.sonyged.hyperClass.utils.formatDate1
import com.sonyged.hyperClass.viewmodel.LessonCreateViewModel
import timber.log.Timber
import java.util.*

class LessonCreateActivity : BaseActivity() {

    private val binding: ActivityLessonCreateBinding by lazy {
        ActivityLessonCreateBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<LessonCreateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        binding.back.setOnClickListener {
            finish()
        }

        binding.date.setOnClickListener {
            startDatePicker()
        }

        binding.startTime.setOnClickListener {
            startTimePicker(true)
        }

        binding.endTime.setOnClickListener {
            startTimePicker(false)
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
                binding.startTime.text = getString(R.string.value_time, timePicker.hour, timePicker.minute)
                binding.startTime.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
            } else {
                binding.endTime.text = getString(R.string.value_time, timePicker.hour, timePicker.minute)
                binding.endTime.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
            }
        }
    }


}