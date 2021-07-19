package com.sonyged.hyperClass.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.activity.SubmissionActivity
import com.sonyged.hyperClass.constants.KEY_WORKOUT_NAME
import com.sonyged.hyperClass.databinding.FragmentWorkoutBinding
import com.sonyged.hyperClass.databinding.ViewItemDetailValueBinding
import com.sonyged.hyperClass.model.Workout
import com.sonyged.hyperClass.utils.previewImageActivity
import com.sonyged.hyperClass.viewmodel.ExerciseViewModel
import timber.log.Timber

class WorkoutFragment : BaseFragment(R.layout.fragment_workout) {

    companion object {

        fun create(): Fragment {
            return WorkoutFragment()
        }
    }

    private val binding: FragmentWorkoutBinding by lazy {
        FragmentWorkoutBinding.bind(requireView())
    }

    private val viewModel: ExerciseViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ExerciseViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.course.text1.setText(R.string.course)
        binding.description.text1.setText(R.string.description)
        binding.term.text1.setText(R.string.workout_term)
//        binding.status.text1.setText(R.string.status)
//        binding.answer.text1.setText(R.string.your_answer)
        binding.file.text1.setText(R.string.submission_file)

        binding.editButton.setOnClickListener {
            startSubmissionActivity()
        }

        viewModel.workout.observe(viewLifecycleOwner) { updateWorkout(it) }
    }

    private fun updateWorkout(workout: Workout) {
        Timber.d("updateWorkout - workout: $workout")

        binding.course.text2.text = workout.courseName
        binding.description.text2.text = workout.description
        binding.term.text2.text = workout.date

        workout.files.forEach { attachment ->
            val fileBinding = ViewItemDetailValueBinding.inflate(LayoutInflater.from(requireContext()))
            fileBinding.root.text = attachment.filename
            val params = LinearLayoutCompat.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            params.topMargin = resources.getDimensionPixelSize(R.dimen.item_detail_value_margin_top)
            fileBinding.root.setOnClickListener {
                previewImageActivity(requireContext(), attachment.url ?: "")
            }
            binding.file.root.addView(fileBinding.root, params)
        }
    }

    private fun startSubmissionActivity() {
        val name = viewModel.workout.value?.name ?: ""
        val intent = Intent(requireContext(), SubmissionActivity::class.java)
        intent.putExtra(KEY_WORKOUT_NAME, name)
        startActivity(intent)
    }


}