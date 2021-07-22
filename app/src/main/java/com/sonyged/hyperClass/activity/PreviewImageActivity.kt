package com.sonyged.hyperClass.activity

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.KEY_URL
import com.sonyged.hyperClass.databinding.ActivityPreviewImageBinding
import com.sonyged.hyperClass.glide.GlideApp
import com.sonyged.hyperClass.glide.MyGlideModule
import timber.log.Timber

class PreviewImageActivity : PreviewFileActivity() {

    private val binding: ActivityPreviewImageBinding by lazy {
        ActivityPreviewImageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        val url = intent.getStringExtra(KEY_URL)
        Timber.d("setupView - url: $url")
        if (url.isNullOrEmpty()) {
            Toast.makeText(applicationContext, R.string.url_null_empty, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.loading.show()
        GlideApp.with(this)
            .asBitmap()
            .load(url)
            .dontTransform()
            .dontAnimate()
            .apply(MyGlideModule.fullRequestOptions())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    setImageToView(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Timber.d("onLoadFailed")
                }

            })
    }

    private fun setImageToView(bitmap: Bitmap) {
        Timber.d("setImageToView - bitmap.isRecycled: ${bitmap.isRecycled} - bitmap.isMutable: ${bitmap.isMutable}")
        Timber.d("setImageToView - bitmap.width: ${bitmap.width} - bitmap.height: ${bitmap.height}")

        if (bitmap.width == 0 || bitmap.height == 0) {
            Timber.e("setImageToView - bitmap size = 0")
            return
        }

        try {
            binding.image.setImage(ImageSource.bitmap(bitmap))
        } catch (e: Exception) {
            Timber.e(e,"setImageToView")
        }
        binding.loading.hide()

    }

}