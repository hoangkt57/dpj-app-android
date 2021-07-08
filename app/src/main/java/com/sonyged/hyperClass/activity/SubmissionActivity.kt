package com.sonyged.hyperClass.activity

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.sonyged.hyperClass.adapter.ImageAdapter
import com.sonyged.hyperClass.adapter.viewholder.OnItemClickListener
import com.sonyged.hyperClass.constants.KEY_STUDENT_WORKOUT_ID
import com.sonyged.hyperClass.constants.KEY_WORKOUT_NAME
import com.sonyged.hyperClass.databinding.ActivitySubmissionBinding
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

        setContentView(binding.root)

        setupView()

        viewModel.images.observe(this) { updateImages(it) }
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


    private fun updateImages(images: List<Uri>) {
        Timber.d("updateImages - size: ${images.size}")

        adapter.submitList(images)

    }

    override fun onItemClick(position: Int) {
        Timber.d("onItemClick - position: $position")

        val uri = adapter.getAdapterItem(position)
        viewModel.deleteImage(uri)
    }
}