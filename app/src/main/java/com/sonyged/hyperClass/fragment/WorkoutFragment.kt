package com.sonyged.hyperClass.fragment

import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.activity.BaseActivity
import com.sonyged.hyperClass.constants.EVENT_WORKOUT_CHANGE
import com.sonyged.hyperClass.constants.STATUS_FAILED
import com.sonyged.hyperClass.constants.STATUS_LOADING
import com.sonyged.hyperClass.constants.STATUS_SUCCESSFUL
import com.sonyged.hyperClass.contract.CreateWorkout
import com.sonyged.hyperClass.contract.CreateWorkoutInput
import com.sonyged.hyperClass.contract.OpenSubmissionWorkout
import com.sonyged.hyperClass.databinding.FragmentWorkoutBinding
import com.sonyged.hyperClass.databinding.ViewItemDetailValueBinding
import com.sonyged.hyperClass.databinding.ViewItemSubmissionFileBinding
import com.sonyged.hyperClass.glide.GlideApp
import com.sonyged.hyperClass.glide.MyGlideModule
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.StatusResource
import com.sonyged.hyperClass.model.Workout
import com.sonyged.hyperClass.observer.AppObserver
import com.sonyged.hyperClass.type.WorkoutStatus
import com.sonyged.hyperClass.utils.formatDate2
import com.sonyged.hyperClass.utils.previewFileActivity
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

        if (!viewModel.isTeacher()) {
            binding.status.root.visibility = View.VISIBLE
            binding.answer.root.visibility = View.VISIBLE
            binding.submissionFile.root.visibility = View.VISIBLE
            binding.comment.root.visibility = View.VISIBLE
        }

        binding.course.text1.setText(R.string.course)
        binding.description.text1.setText(R.string.description)
        binding.deadline.text1.setText(R.string.deadline)
        binding.material.text1.setText(R.string.material)
        binding.status.text1.setText(R.string.status)
        binding.answer.text1.setText(R.string.your_answer)
        binding.submissionFile.text1.setText(R.string.submission_file)
        binding.comment.text1.setText(R.string.comment_from_teacher)

        binding.editButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.anim_edit_close
            )
        )
        binding.editButton.setOnClickListener {
            if (viewModel.isTeacher()) {
                if (binding.editButton.isSelected) {
                    extendFab()
                } else {
                    shrinkFab()
                }
            } else {
                viewModel.workout.value?.let {
                    openSubmissionWorkout.launch(it)
                }
            }
        }

        binding.deleteButton.setOnClickListener {
            extendFab()
            viewModel.workout.value?.let {
                deleteWorkoutDialog(it)
            }
        }

        binding.changeButton.setOnClickListener {
            extendFab()
            viewModel.workout.value?.let {
                createWorkout.launch(CreateWorkoutInput(workout = it))
            }
        }

        binding.verify.setOnClickListener {
            viewModel.workout.value?.studentWorkoutId?.let {
                viewModel.verifyWorkout(it)
            }
        }

        viewModel.workout.observe(viewLifecycleOwner) { updateWorkout(it) }
        viewModel.status.observe(viewLifecycleOwner) { updateStatus(it) }
        viewModel.info.observe(viewLifecycleOwner) { updateInfo(it) }
    }

    private fun updateInfo(info: Triple<String, String, String>) {
        binding.course.text2.text = info.third
    }

    private fun updateStatus(status: Status) {
        when (status.id) {
            STATUS_LOADING -> {
                if (activity is BaseActivity) {
                    (activity as BaseActivity).showProgressDialog()
                }
            }
            STATUS_FAILED -> {
                if (activity is BaseActivity) {
                    (activity as BaseActivity).hideProgressDialog()
                }
            }
            STATUS_SUCCESSFUL -> {
                activity?.let {
                    if (activity is BaseActivity) {
                        (activity as BaseActivity).hideProgressDialog()
                    }
                    Toast.makeText(it.applicationContext, R.string.deleted, Toast.LENGTH_SHORT).show()
                    AppObserver.getInstance().sendEvent(EVENT_WORKOUT_CHANGE)
                    it.finish()
                }
            }
        }
    }

    private fun updateWorkout(workout: Workout) {
        Timber.d("updateWorkout - workout: $workout")
        val context = context ?: return
        binding.description.text2.text = workout.description
        binding.deadline.text2.text = formatDate2(workout.date)
        binding.material.value.removeAllViews()
        workout.files.forEach { attachment ->
            val fileBinding = ViewItemDetailValueBinding.inflate(LayoutInflater.from(context))
            fileBinding.root.text = attachment.filename
            val params = LinearLayoutCompat.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            params.topMargin = resources.getDimensionPixelSize(R.dimen.item_detail_value_margin_top)
            fileBinding.root.setOnClickListener {
                previewFileActivity(context, attachment)
            }
            binding.material.value.addView(fileBinding.root, params)
        }
        if (!viewModel.isTeacher()) {
            val statusValues = StatusResource.getStatus(context, workout.status)
            if (statusValues != null) {
                binding.status.text3.text = statusValues.text
                binding.status.text3.setTextColor(ContextCompat.getColor(context, statusValues.textColor))
                binding.status.text3.setBackgroundColor(ContextCompat.getColor(context, statusValues.bgColor))
            }
            binding.status.text2.text = formatDate2(workout.submittedAt)
            binding.answer.text2.text = workout.answer
            binding.submissionFile.value.removeAllViews()
            workout.submissionFile.forEach { attachment ->
                val fileBinding = ViewItemSubmissionFileBinding.inflate(LayoutInflater.from(context))
                fileBinding.text2.text = attachment.filename
                fileBinding.text3.text = formatDate2(attachment.createAt)
                GlideApp.with(fileBinding.image)
                    .load(Uri.parse(attachment.url))
                    .placeholder(R.drawable.bg_image)
                    .error(R.drawable.ic_error_file)
                    .apply(MyGlideModule.fullRequestOptions())
                    .into(fileBinding.image)
                fileBinding.root.setOnClickListener {
                    previewFileActivity(context, attachment)
                }
                binding.submissionFile.value.addView(fileBinding.root)
            }
            binding.comment.text2.text = workout.comment
            binding.comment.divider.visibility = View.GONE
            if (workout.status == WorkoutStatus.REVIEWED) {
                binding.verify.visibility = View.VISIBLE
            } else if (workout.status == WorkoutStatus.COMPLETED) {
                binding.editButton.visibility = View.GONE
            }
        }
    }

    private fun shrinkFab() {
        binding.editButton.isSelected = true
        binding.editButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.anim_edit_close
            )
        )
        if (binding.editButton.drawable is AnimatedVectorDrawable) {
            (binding.editButton.drawable as AnimatedVectorDrawable).start()
        }
        binding.changeButton.show()
        binding.deleteButton.show()
    }

    private fun extendFab() {
        binding.editButton.isSelected = false
        binding.editButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.anim_close_edit
            )
        )
        if (binding.editButton.drawable is AnimatedVectorDrawable) {
            (binding.editButton.drawable as AnimatedVectorDrawable).start()
        }
        binding.deleteButton.hide()
        binding.changeButton.hide()
    }

    private fun deleteWorkoutDialog(workout: Workout) {
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.AddStudentDialog)
        builder.setTitle(R.string.do_you_want_delete_workout)
        builder.setMessage(R.string.do_you_want_delete_workout_msg)
        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            viewModel.deleteWorkout(workout.id)
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private val createWorkout = registerForActivityResult(CreateWorkout()) {}
    private val openSubmissionWorkout = registerForActivityResult(OpenSubmissionWorkout()) {}

}