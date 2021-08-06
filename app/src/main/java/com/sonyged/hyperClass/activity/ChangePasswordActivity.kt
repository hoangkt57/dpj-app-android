package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.ActivityChangePasswordBinding
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.utils.startMainActivity
import com.sonyged.hyperClass.viewmodel.ChangePasswordViewModel
import timber.log.Timber

class ChangePasswordActivity : BaseActivity() {

    private val binding: ActivityChangePasswordBinding by lazy {
        ActivityChangePasswordBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<ChangePasswordViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val changePassword = intent.getBooleanExtra(KEY_CHANGE_PASSWORD_FIRST, false)
        if (changePassword) {
            viewModel.loadCurrentPassword()
        }

        setupView()

        viewModel.status.observe(this) { updateStatus(it) }
        viewModel.password.observe(this) { updatePassword(it) }
    }

    private fun setupView() {
        binding.back.setOnClickListener {
            finish()
        }

        binding.showPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.oldPwEdit.transformationMethod = null
                binding.newPwEdit.transformationMethod = null
                binding.newPwEdit1.transformationMethod = null
            } else {
                binding.oldPwEdit.transformationMethod = PasswordTransformationMethod()
                binding.newPwEdit.transformationMethod = PasswordTransformationMethod()
                binding.newPwEdit1.transformationMethod = PasswordTransformationMethod()
            }
        }

        binding.change.setOnClickListener {
            submitPassword()
        }
    }

    private fun updatePassword(password: String) {
        Timber.d("updatePassword - password: $password")
        binding.oldPwEdit.setText(password)
    }

    private fun updateStatus(status: Status) {
        Timber.d("updateStatus - status: $status")
        when (status.id) {
            STATUS_LOADING -> {
                showProgressDialog()
                binding.error.visibility = View.GONE
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
                val changePassword = intent.getBooleanExtra(KEY_CHANGE_PASSWORD_FIRST, false)
                if (changePassword) {
                    viewModel.setLoginSuccess()
                    startMainActivity(this)
                } else {
                    Toast.makeText(applicationContext, R.string.password_changed, Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }

    private fun submitPassword() {
        val oldPass = binding.oldPwEdit.text.toString()
        val newPass = binding.newPwEdit.text.toString()
        val newPassConfirm = binding.newPwEdit1.text.toString()
        if (viewModel.isValidPassword(oldPass, newPass, newPassConfirm)) {
            val id = intent.getStringExtra(KEY_USER_ID)
            if (id.isNullOrEmpty()) {
                finish()
                return
            }
            viewModel.changePassword(id, oldPass, newPass)
        }
    }


}