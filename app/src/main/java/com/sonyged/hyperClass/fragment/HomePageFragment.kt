package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.style.AbsoluteSizeSpan
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.activity.MainActivity
import com.sonyged.hyperClass.adapter.ExerciseAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.adapter.viewholder.OnMoreClickListener
import com.sonyged.hyperClass.contract.OpenLesson
import com.sonyged.hyperClass.contract.OpenWorkout
import com.sonyged.hyperClass.databinding.FragmentHomeBinding
import com.sonyged.hyperClass.model.BaseItem
import com.sonyged.hyperClass.model.ExerciseFilter
import com.sonyged.hyperClass.type.UserEventFilterType
import com.sonyged.hyperClass.utils.formatDayWithName
import com.sonyged.hyperClass.viewmodel.MainViewModel
import com.sonyged.hyperClass.views.ExerciseSpaceItemDecoration
import timber.log.Timber
import java.util.*


class HomePageFragment : BaseFragment(R.layout.fragment_home), OnItemClickListener, OnTitleClickListener, OnMoreClickListener {

    companion object {

        fun create(): Fragment {
            return HomePageFragment()
        }
    }

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.bind(requireView())
    }

    private val adapter: ExerciseAdapter by lazy {
        ExerciseAdapter()
    }

    private val viewModel by viewModels<MainViewModel>(ownerProducer = { requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setOnItemClickListener(this)
        adapter.setOnMoreClickListener(this)
        binding.recyclerView.apply {
            addItemDecoration(ExerciseSpaceItemDecoration(requireContext()))
            adapter = this@HomePageFragment.adapter
        }

        binding.loading.show()
        binding.filter.setOnClickListener {
            changeFilter()
        }

        viewModel.exercises.observe(viewLifecycleOwner) { updateExercises(it) }
        viewModel.exerciseFilter.observe(viewLifecycleOwner) { updateDateRange(it) }
    }

    private fun updateExercises(list: List<BaseItem>) {
        Timber.d("updateExercises - size: ${list.size}")
        adapter.submitList(list)
        binding.loading.hide()
        binding.recyclerView.visibility = View.VISIBLE
    }

    override fun onItemClick(position: Int) {
        Timber.d("onItemClick - position: $position")

        val exercise = adapter.getAdapterItem(position) ?: return
        if (exercise.type == UserEventFilterType.LESSON) {
            openLesson.launch(exercise)
        } else if (exercise.type == UserEventFilterType.WORKOUT) {
            openWorkout.launch(exercise)
        }
    }

    override fun showMore() {
        Timber.d("showMore")
        viewModel.showMore()
    }

    override fun onTitleClick() {
        Timber.d("onTitleClick")

        val dateRangePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
        dateRangePickerBuilder.setTitleText(R.string.picker_range_header_title)

        val dateRangePicker = dateRangePickerBuilder.build()
        dateRangePicker.show(childFragmentManager, "dateRangePicker")

        dateRangePicker.addOnPositiveButtonClickListener { date ->
            Timber.d("onTitleClick - change date: $date")

            binding.loading.show()
            binding.recyclerView.visibility = View.INVISIBLE
            viewModel.setDateRange(Pair(date.first, date.second))
        }
    }

    private fun updateDateRange(filter: ExerciseFilter) {
        val date = filter.dateRange ?: return
        val rangeDate = DateUtils.formatDateRange(
            requireContext(),
            date.first,
            date.second,
            DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_NO_MONTH_DAY
        )
        binding.dayRange.text = formatDayRange(date)
        if (activity is MainActivity) {
            (activity as MainActivity).setTitleDateRange(rangeDate)
        }
    }

    private fun changeFilter() {
        binding.loading.show()
        binding.recyclerView.visibility = View.INVISIBLE
        when (viewModel.exerciseFilter.value?.type) {
            UserEventFilterType.ALL -> {
                binding.filter.setText(R.string.lesson)
                viewModel.setType(UserEventFilterType.LESSON)
            }
            UserEventFilterType.LESSON -> {
                binding.filter.setText(R.string.workout)
                viewModel.setType(UserEventFilterType.WORKOUT)
            }
            else -> {
                binding.filter.setText(R.string.all)
                viewModel.setType(UserEventFilterType.ALL)
            }
        }
    }

    private fun formatDayRange(date: Pair<Long, Long>): CharSequence {
        try {
            val textSize1 = resources.getDimensionPixelSize(R.dimen.home_day_range_text_size_big)
            val textSize2 = resources.getDimensionPixelSize(R.dimen.home_day_range_text_size_medium)
            val textSize3 = resources.getDimensionPixelSize(R.dimen.home_day_range_text_size_small)
            val day1 = formatDayWithName(date.first)
            val day2 = formatDayWithName(date.second)
            val split1 = day1.split(" ")
            val split2 = day2.split(" ")
            val span1 = SpannableString(split1[0])
            span1.setSpan(AbsoluteSizeSpan(textSize1), 0, span1.length, SPAN_INCLUSIVE_INCLUSIVE)
            val span2 = SpannableString(split2[0])
            span2.setSpan(AbsoluteSizeSpan(textSize2), 0, span2.length, SPAN_INCLUSIVE_INCLUSIVE)
            val span3 = SpannableString(split1[1] + " ~")
            span3.setSpan(AbsoluteSizeSpan(textSize3), 0, span3.length, SPAN_INCLUSIVE_INCLUSIVE)
            val span4 = SpannableString(split2[1])
            span4.setSpan(AbsoluteSizeSpan(textSize3), 0, span4.length, SPAN_INCLUSIVE_INCLUSIVE)

            return TextUtils.concat(span1, " ", span3, " ", span2, " ", span4)
        } catch (e: Exception) {
            Timber.e(e, "formatDayRange")
        }
        return ""
    }

    private val openLesson = registerForActivityResult(OpenLesson()) {}
    private val openWorkout = registerForActivityResult(OpenWorkout()) {}

}