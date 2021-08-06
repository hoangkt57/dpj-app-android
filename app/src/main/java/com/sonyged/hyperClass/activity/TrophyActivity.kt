package com.sonyged.hyperClass.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.KEY_COURSE_ID
import com.sonyged.hyperClass.databinding.ActivityTrophyBinding
import com.sonyged.hyperClass.model.StudentTrophy
import com.sonyged.hyperClass.viewmodel.TrophyViewModel
import com.sonyged.hyperClass.viewmodel.TrophyViewModelFactory
import timber.log.Timber

class TrophyActivity : BaseActivity() {

    private val binding: ActivityTrophyBinding by lazy {
        ActivityTrophyBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<TrophyViewModel>() {
        val courseId = intent.getStringExtra(KEY_COURSE_ID) ?: ""
        TrophyViewModelFactory(application, courseId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        binding.trophy.icon.setImageResource(R.drawable.ic_trophy)
        binding.trophy.name.setText(R.string.trophy_won)
        binding.trophy.point.setTextColor(ContextCompat.getColor(this, R.color.trophy_progress_indicator))
        binding.trophy.progress.setIndicatorColor(ContextCompat.getColor(this, R.color.trophy_progress_indicator))
        binding.speak.icon.setImageResource(R.drawable.ic_speak)
        binding.speak.name.setText(R.string.number_remark)
        binding.trophy.point.setTextColor(ContextCompat.getColor(this, R.color.speak_progress_indicator))
        binding.trophy.progress.setIndicatorColor(ContextCompat.getColor(this, R.color.speak_progress_indicator))
        binding.hand.icon.setImageResource(R.drawable.ic_raisehand)
        binding.hand.name.setText(R.string.number_hand_raise)
        binding.hand.point.setTextColor(ContextCompat.getColor(this, R.color.hand_progress_indicator))
        binding.hand.progress.setIndicatorColor(ContextCompat.getColor(this, R.color.hand_progress_indicator))

        viewModel.studentTrophy.observe(this) { updateStudentTrophy(it) }
    }

    private fun updateStudentTrophy(data: StudentTrophy) {
        Timber.d("updateStudentTrophy - data: $data")

        binding.title.text = data.courseName
        binding.name.text = data.name
        binding.trophy.progress.max = data.trophyMaxCount
        binding.trophy.progress.progress = data.trophyCount
        binding.trophy.point.text = data.trophyCount.toString()
        if (data.trophyCount >= data.trophyMaxCount) {
            binding.trophy.target.text = getString(R.string.reach_goal)
            binding.trophy.target.isSelected = true
        } else {
            binding.trophy.target.text = getString(R.string.goal, data.trophyMaxCount)
            binding.trophy.target.isSelected = false
        }
        binding.trophy.percent.text = getString(R.string.percent, data.getTrophyPercent())
        binding.trophy.percent.isSelected = binding.trophy.target.isSelected

        binding.speak.progress.max = data.speakMaxDurationInSec / 60
        binding.speak.progress.progress = data.speakDurationInSec / 60
        binding.speak.point.text = (data.speakDurationInSec / 60).toString()
        if (data.speakDurationInSec >= data.speakMaxDurationInSec) {
            binding.speak.target.text = getString(R.string.reach_goal)
            binding.speak.target.isSelected = true
        } else {
            binding.speak.target.text = getString(R.string.goal, data.speakMaxDurationInSec / 60)
            binding.speak.target.isSelected = false
        }
        binding.speak.percent.isSelected = binding.speak.target.isSelected
        binding.speak.percent.text = getString(R.string.percent, data.getSpeakPercent())

        binding.hand.progress.max = data.handMaxCount
        binding.hand.progress.progress = data.handCount
        binding.hand.point.text = data.handCount.toString()
        if (data.handCount >= data.handMaxCount) {
            binding.hand.target.text = getString(R.string.reach_goal)
            binding.hand.target.isSelected = true
        } else {
            binding.hand.target.text = getString(R.string.goal, data.handMaxCount)
            binding.hand.target.isSelected = false
        }
        binding.hand.percent.isSelected = binding.hand.target.isSelected
        binding.hand.percent.text = getString(R.string.percent, data.getHandPercent())
    }

}