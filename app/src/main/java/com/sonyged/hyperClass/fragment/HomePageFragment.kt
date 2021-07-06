package com.sonyged.hyperClass.fragment

import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.activity.MainActivity
import com.sonyged.hyperClass.adapter.ExerciseAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.databinding.FragmentHomeBinding
import com.sonyged.hyperClass.model.Exercise
import com.sonyged.hyperClass.type.UserEventFilterType
import com.sonyged.hyperClass.viewmodel.MainViewModel
import com.sonyged.hyperClass.views.ExerciseSpaceItemDecoration
import timber.log.Timber
import java.util.*

class HomePageFragment : BaseFragment(R.layout.fragment_home), OnItemClickListener, OnTitleClickListener {

    companion object {

        fun create(): Fragment {
            return HomePageFragment()
        }
    }

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.bind(requireView())
    }

    private val adapter: ExerciseAdapter by lazy {
        ExerciseAdapter(this)
    }

    private val viewModel by viewModels<MainViewModel>(ownerProducer = { requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            addItemDecoration(ExerciseSpaceItemDecoration(requireContext()))
            adapter = this@HomePageFragment.adapter
        }

        binding.filter.setOnClickListener {
            changeFilter()
        }

        viewModel.exercises.observe(viewLifecycleOwner) { updateExercises(it) }
    }

    private fun updateExercises(exercises: List<Exercise>) {
        Timber.d("updateExercises - size: ${exercises.size}")

        adapter.submitList(exercises)

    }

    override fun onItemClick(position: Int) {
        Timber.d("onItemClick - position: $position")
    }

    override fun onTitleClick() {
        Timber.d("onTitleClick")

        Locale.setDefault(Locale.JAPAN)
        activity?.resources?.configuration?.setLocale(Locale.JAPAN)

        val dateRangePickerBuilder = MaterialDatePicker.Builder.dateRangePicker();

        val dateRangePicker = dateRangePickerBuilder.build()
        dateRangePicker.show(childFragmentManager, "dateRangePicker")

        dateRangePicker.addOnPositiveButtonClickListener { date ->
            Timber.d("onTitleClick - change date: $date")

            val startDate = Date(date.first)
            val endDate = Date(date.second)

            val rangeDate = DateUtils.formatDateRange(
                requireContext(),
                date.first,
                date.second,
                DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_NO_MONTH_DAY
            )

            Timber.d("onTitleClick - startDate: $startDate - endDate: $endDate - rangeDate: $rangeDate")

            if (activity is MainActivity) {
                (activity as MainActivity).setTitleDateRange(rangeDate)
            }

            viewModel.dateRange.postValue(Pair(date.first, date.second))

        }
    }

    private fun changeFilter() {
        when (viewModel.type.value) {
            UserEventFilterType.ALL -> {
                binding.filter.setText(R.string.lesson)
                viewModel.type.postValue(UserEventFilterType.LESSON)
            }
            UserEventFilterType.LESSON -> {
                binding.filter.setText(R.string.workout)
                viewModel.type.postValue(UserEventFilterType.WORKOUT)
            }
            else -> {
                binding.filter.setText(R.string.all)
                viewModel.type.postValue(UserEventFilterType.ALL)
            }
        }
    }

}