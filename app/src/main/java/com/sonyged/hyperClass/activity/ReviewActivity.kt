package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.ReviewPageAdapter
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.ActivityReviewBinding
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.StudentWorkout
import com.sonyged.hyperClass.observer.AppObserver
import com.sonyged.hyperClass.viewmodel.ReviewViewModel
import com.sonyged.hyperClass.viewmodel.ReviewViewModelFactory
import timber.log.Timber

class ReviewActivity : BaseActivity() {

    private val binding: ActivityReviewBinding by lazy {
        ActivityReviewBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<ReviewViewModel> {
        val id = intent.getStringExtra(KEY_STUDENT_WORKOUT_ID) ?: ""
        val ids = intent.getStringArrayListExtra(KEY_STUDENT_WORKOUT_LIST_ID) ?: arrayListOf()
        ReviewViewModelFactory(application, id, ids)
    }

    private val adapter: ReviewPageAdapter by lazy {
        ReviewPageAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.back.setOnClickListener {
            finish()
        }
        binding.prev.isSelected = true
        binding.next.isSelected = true
        binding.next.setOnClickListener {
            next()
        }
        binding.prev.setOnClickListener {
            prev()
        }
        binding.viewPager.apply {
            adapter = this@ReviewActivity.adapter
            isUserInputEnabled = false
            offscreenPageLimit = 3
            registerOnPageChangeCallback(callBack)
        }

        viewModel.studentWorkouts.observe(this) { updateStudentWorkout(it) }
        viewModel.status.observe(this) { updateStatus(it) }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.viewPager.unregisterOnPageChangeCallback(callBack)
    }

    private fun updateStudentWorkout(data: List<StudentWorkout>) {
        Timber.d("updateStudentWorkout - size: ${data.size}")
        val currentId = adapter.getItemId(binding.viewPager.currentItem)
        val newPosition = (data.indices).indexOfFirst { data[it].getIdToLong() == currentId }
        adapter.submitList(data)
        if (newPosition != -1) {
            binding.viewPager.setCurrentItem(newPosition, false)
        }
    }

    private fun prev() {
        if (binding.viewPager.currentItem == 0) {
            return
        }
        binding.viewPager.currentItem = binding.viewPager.currentItem - 1
    }

    private fun next() {
        if (binding.viewPager.currentItem == adapter.itemCount - 1) {
            return
        }
        binding.viewPager.currentItem = binding.viewPager.currentItem + 1
    }

    private fun updateStatus(status: Status) {
        when (status.id) {
            STATUS_LOADING -> {
                showProgressDialog()
            }
            STATUS_FAILED -> {
                hideProgressDialog()
            }
            STATUS_SUCCESSFUL -> {
                hideProgressDialog()
                Toast.makeText(this, R.string.i_reviewed, Toast.LENGTH_SHORT).show()
                AppObserver.getInstance().sendEvent(EVENT_REVIEW_CHANGE)
                finish()
            }
        }
    }

    private val callBack = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            Timber.d("onPageSelected - position: $position")
            if (adapter.itemCount == 1) {
                binding.prev.isSelected = false
                binding.next.isSelected = false
                return
            }
            when (position) {
                0 -> {
                    binding.prev.isSelected = false
                    binding.next.isSelected = true
                }
                adapter.itemCount - 1 -> {
                    binding.next.isSelected = false
                    binding.prev.isSelected = true
                }
                else -> {
                    binding.prev.isSelected = true
                    binding.next.isSelected = true
                }
            }
        }
    }
}