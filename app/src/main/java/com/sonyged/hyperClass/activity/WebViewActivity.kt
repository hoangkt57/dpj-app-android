package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.KEY_URL
import com.sonyged.hyperClass.databinding.ActivityWebViewBinding


class WebViewActivity : BaseActivity() {

    private val binding: ActivityWebViewBinding by lazy {
        ActivityWebViewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {

        val url = intent.getStringExtra(KEY_URL)
        if (url.isNullOrEmpty()) {
            Toast.makeText(applicationContext, R.string.url_null_empty, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.settings.mediaPlaybackRequiresUserGesture = false
        binding.webView.loadUrl(url)
    }

}