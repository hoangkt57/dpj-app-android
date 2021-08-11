package com.sonyged.hyperClass.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.FragmentReviewBinding
import com.sonyged.hyperClass.databinding.ViewItemSubmissionFileBinding
import com.sonyged.hyperClass.glide.GlideApp
import com.sonyged.hyperClass.glide.MyGlideModule
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.StudentWorkout
import com.sonyged.hyperClass.type.WorkoutReviewStatus
import com.sonyged.hyperClass.type.WorkoutStatus
import com.sonyged.hyperClass.utils.formatDate1
import com.sonyged.hyperClass.utils.formatDate2
import com.sonyged.hyperClass.utils.previewFileActivity
import com.sonyged.hyperClass.viewmodel.ReviewViewModel

class ReviewFragment : BaseFragment(R.layout.fragment_review) {

    companion object {
        fun create(data: StudentWorkout): Fragment {
            val bundle = Bundle()
            val fragment = ReviewFragment()
            bundle.putParcelable(KEY_STUDENT_WORKOUT, data)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val binding: FragmentReviewBinding by lazy {
        FragmentReviewBinding.bind(requireView())
    }

    private val viewModel by viewModels<ReviewViewModel>(ownerProducer = { requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = arguments?.getParcelable<StudentWorkout>(KEY_STUDENT_WORKOUT)
        val context = context
        if (data != null && context != null) {
            binding.studentName.text = data.studentName
            binding.date.text = formatDate1(data.submittedAt)
            binding.answer.text = data.answer
            binding.files.removeAllViews()
            data.attachments.forEach { attachment ->
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
                binding.files.addView(fileBinding.root)
            }

            if (data.status == WorkoutStatus.REVIEWED.rawValue || data.status == WorkoutStatus.REJECTED.rawValue
                || data.status == WorkoutStatus.COMPLETED.rawValue) {
                binding.comment.visibility = View.VISIBLE
                binding.comment.text = data.comment
            } else if (data.status == WorkoutStatus.SUBMITTED.rawValue) {
                binding.buttonLayout.visibility = View.VISIBLE
                binding.resubmit.visibility = View.VISIBLE
                binding.sendReview.visibility = View.VISIBLE
            }

        }

        val buttons = arrayListOf(binding.goodJob, binding.confirmed, binding.checkAgain, binding.addComment)
        buttons.forEach { button ->
            button.setOnClickListener {
                selectButton(it)
            }
        }

        binding.sendReview.isEnabled = false
        binding.sendReview.setOnClickListener {
            if (!binding.sendReview.isEnabled) return@setOnClickListener
            data?.let {
                viewModel.sendReview(
                    data.id,
                    getReviewStatus(),
                    getReviewComment()
                )
            }
        }

        viewModel.status.observe(viewLifecycleOwner) { updateStatus(it) }
    }

    private fun selectButton(button: View) {
        val buttons = arrayListOf(binding.goodJob, binding.confirmed, binding.checkAgain, binding.addComment)
        buttons.forEach {
            it.isSelected = it.id == button.id
        }
        if (button.id == binding.addComment.id) {
            binding.commentTextField.visibility = View.VISIBLE
        } else {
            binding.commentTextField.visibility = View.GONE
        }
        binding.sendReview.isEnabled = true
        binding.sendReview.alpha = 1f
    }

    private fun getReviewStatus(): WorkoutReviewStatus {
        if (binding.resubmit.isChecked) {
            return WorkoutReviewStatus.REJECTED
        }
        if (binding.goodJob.isSelected || binding.confirmed.isSelected) {
            return WorkoutReviewStatus.REVIEWED
        }
        return WorkoutReviewStatus.REJECTED
    }

    private fun getReviewComment(): String {
        if (binding.addComment.isChecked) {
            return binding.commentEdit.text.toString()
        }
        if (binding.goodJob.isSelected) {
            return binding.goodJob.text.toString()
        }
        if (binding.confirmed.isSelected) {
            return binding.confirmed.text.toString()
        }
        if (binding.checkAgain.isSelected) {
            return binding.checkAgain.text.toString()
        }
        return ""
    }

    private fun updateStatus(status: Status) {
        when (status.id) {
            STATUS_FAILED -> {
                val error = status.extras.getString(KEY_ERROR_MSG) ?: ""
                if (error.isNotEmpty()) {
                    binding.error.text = error
                    binding.error.visibility = View.VISIBLE
                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }
    }

}