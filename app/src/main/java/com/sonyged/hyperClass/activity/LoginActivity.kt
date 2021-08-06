package com.sonyged.hyperClass.activity

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.activity.viewModels
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.ActivityLoginBinding
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.utils.changePasswordActivityFirst
import com.sonyged.hyperClass.utils.startAgreementPpActivity
import com.sonyged.hyperClass.utils.startMainActivity
import com.sonyged.hyperClass.viewmodel.LoginViewModel
import timber.log.Timber

class LoginActivity : BaseActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        if (viewModel.isLogin()) {
            startMainActivity(this)
            overridePendingTransition(0, 0)
            finish()
            return
        }

        binding.contentLayout.postDelayed({
            binding.idField.visibility = View.VISIBLE
            binding.passwordField.visibility = View.VISIBLE
            binding.showPassword.visibility = View.VISIBLE
            binding.login.visibility = View.VISIBLE

        }, 500)

        binding.showPassword.setOnCheckedChangeListener { _, isChecked ->
            viewModel.currentUser()
            if (isChecked) {
                binding.passwordEdittext.transformationMethod = null
            } else {
                binding.passwordEdittext.transformationMethod = PasswordTransformationMethod()
            }
        }

        binding.login.setOnClickListener {
            viewModel.login(
                binding.idEdittext.text?.toString(),
                binding.passwordEdittext.text?.toString()
            )
        }

//        binding.idEdittext.setText("teacher0003@sctest")
//        binding.idEdittext.setText("s0008@hcdemo")
//        binding.idEdittext.setText("t0036@hcdemo")
        binding.idEdittext.setText("student0000@sctest")
        binding.passwordEdittext.setText("indigo123")
//        binding.passwordEdittext.setText("rmuct298")

        viewModel.status.observe(this) { updateStatus(it) }

    }

    private fun updateStatus(status: Status) {
        Timber.d("updateState - status: $status")
        when (status.id) {
            STATUS_LOADING -> {
                showProgressDialog()
                binding.error.visibility = View.GONE
            }
            STATUS_FAILED -> {
                hideProgressDialog()
                binding.error.visibility = View.VISIBLE
            }
            STATUS_SUCCESSFUL -> {
                viewModel.setLoginSuccess()
                startMainActivity(this)
                finish()
            }
            LOGIN_CHANGE_PASSWORD -> {
                handleFirstLogin(agreementPP = false, changePassword = true)
            }
            LOGIN_AGREEMENT_PP -> {
                handleFirstLogin(agreementPP = true, changePassword = false)
            }
            LOGIN_BOTH -> {
                handleFirstLogin(agreementPP = true, changePassword = true)
            }
        }
    }

    private fun handleFirstLogin(agreementPP: Boolean, changePassword: Boolean) {
        if (agreementPP) {
            startAgreementPpActivity(this, viewModel.userId, changePassword)
        } else {
            changePasswordActivityFirst(this, viewModel.userId)
        }
        finish()
    }


}