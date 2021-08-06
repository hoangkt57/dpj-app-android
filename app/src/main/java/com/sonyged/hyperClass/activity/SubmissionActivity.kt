package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.adapter.ImageAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.ActivitySubmissionBinding
import com.sonyged.hyperClass.databinding.ViewItemSubmissionFileBinding
import com.sonyged.hyperClass.model.Attachment
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.model.Workout
import com.sonyged.hyperClass.observer.AppObserver
import com.sonyged.hyperClass.utils.previewFileActivity
import com.sonyged.hyperClass.viewmodel.SubmissionViewModel
import com.sonyged.hyperClass.viewmodel.SubmissionViewModelFactory
import com.sonyged.hyperClass.views.SubmissionSpaceItemDecoration
import timber.log.Timber

class SubmissionActivity : BaseActivity(), OnItemClickListener {

    private val binding: ActivitySubmissionBinding by lazy {
        ActivitySubmissionBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<SubmissionViewModel> {
        val id = intent.getStringExtra(KEY_STUDENT_WORKOUT_ID) ?: ""
        SubmissionViewModelFactory(application, id)
    }

    private val adapter: ImageAdapter by lazy {
        ImageAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getParcelableExtra<Workout>(KEY_WORKOUT)?.let {
            viewModel.workout.postValue(it)
            viewModel.initAttachments(it)
        }

        setContentView(binding.root)
        setupView()

        viewModel.attachments.observe(this) { updateAttachments(it) }
        viewModel.workout.observe(this) { updateWorkout(it) }
        viewModel.status.observe(this) { updateStatus(it) }
    }

    private fun setupView() {
        binding.title.text = intent.getStringExtra(KEY_WORKOUT_NAME) ?: ""
        binding.recyclerView.apply {
            addItemDecoration(SubmissionSpaceItemDecoration(this@SubmissionActivity))
            adapter = this@SubmissionActivity.adapter
        }
        binding.back.setOnClickListener {
            finish()
        }
        binding.camera.setOnClickListener {
            takePicture()
        }
        binding.library.setOnClickListener {
            pickImage()
        }
        binding.submit.setOnClickListener {
            addSubmissionDialog()
        }
    }

    private fun updateWorkout(workout: Workout) {
        binding.title.text = workout.name
        binding.description.text = workout.description
        binding.file.removeAllViews()
        workout.files.forEach { attachment ->
            val fileBinding = ViewItemSubmissionFileBinding.inflate(layoutInflater)
            fileBinding.text2.text = attachment.filename
            fileBinding.text2.setTextColor(ContextCompat.getColor(this, R.color.color_primary_variant))
            fileBinding.text3.visibility = View.GONE
            fileBinding.root.setOnClickListener {
                previewFileActivity(this, attachment)
            }
            binding.file.addView(fileBinding.root)
        }
        binding.answerEdit.setText(workout.answer)
    }

    private fun updateStatus(status: Status) {
        when (status.id) {
            STATUS_LOADING -> {
                showProgressDialog()
                binding.error.visibility = View.INVISIBLE
            }
            STATUS_FAILED -> {
                hideProgressDialog()
                val error = status.extras.getString(KEY_ERROR_MSG) ?: ""
                if (error.isNotEmpty()) {
                    binding.error.text = error
                    binding.error.visibility = View.VISIBLE
                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
            STATUS_SUCCESSFUL -> {
                hideProgressDialog()
                Toast.makeText(applicationContext, R.string.created, Toast.LENGTH_SHORT).show()
                AppObserver.getInstance().sendEvent(EVENT_WORKOUT_CHANGE)
                finish()
            }
        }
    }

    private val registerTakePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        Timber.d("registerTakePicture - success: $success - uri: ${viewModel.imageUri}")

        if (success) {
            viewModel.addCaptureImage()
        }
    }

    private val registerPickImage = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        Timber.d("registerPickImage - size: ${uris.size}")

        viewModel.addImage(uris)
    }

    private fun takePicture() {
        try {
            registerTakePicture.launch(viewModel.pickUri())
        } catch (e: Exception) {
            Timber.e("e")
        }
    }

    private fun pickImage() {
        try {
            registerPickImage.launch("image/*")
        } catch (e: Exception) {
            Timber.e("e")
        }
    }


    private fun updateAttachments(attachments: List<Attachment>) {
        Timber.d("updateAttachments - size: ${attachments.size}")

        adapter.submitList(attachments)
    }

    private fun addSubmissionDialog() {
        val builder = MaterialAlertDialogBuilder(this, R.style.AddStudentDialog)
        builder.setMessage(R.string.submit_submission_title)
        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            viewModel.createSubmission(binding.answerEdit.text.toString())
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onItemClick(position: Int) {
        Timber.d("onItemClick - position: $position")

        val uri = adapter.getAdapterItem(position)
        viewModel.deleteImage(uri)
    }
}