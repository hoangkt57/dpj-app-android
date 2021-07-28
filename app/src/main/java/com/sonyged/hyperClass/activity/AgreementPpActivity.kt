package com.sonyged.hyperClass.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.ActivityAgreementBinding
import com.sonyged.hyperClass.model.Status
import com.sonyged.hyperClass.utils.changePasswordActivityFirst
import com.sonyged.hyperClass.utils.openWeb
import com.sonyged.hyperClass.utils.startMainActivity
import com.sonyged.hyperClass.viewmodel.AgreementViewModel
import timber.log.Timber

class AgreementPpActivity : BaseActivity() {

    private val binding: ActivityAgreementBinding by lazy {
        ActivityAgreementBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<AgreementViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        binding.term.setOnClickListener {
            openWeb(this, LINK_TERM)
        }

        binding.policy.setOnClickListener {
            openWeb(this, LINK_POLICY)
        }

        binding.agree.setOnClickListener {
            viewModel.agreement()
        }

        viewModel.status.observe(this) { updateStatus(it) }

    }

    private fun updateStatus(status: Status) {
        Timber.d("updateStatus - status: $status")

        if (status.id == LOGIN_AGREEMENT_PP) {
            val changePassword = intent.getBooleanExtra(KEY_CHANGE_PASSWORD_FIRST, false)
            if (changePassword) {
                val id = intent.getStringExtra(KEY_USER_ID) ?: ""
                changePasswordActivityFirst(this, id)
            } else {
                viewModel.setLoginSuccess()
                startMainActivity(this)
            }
            finish()
        }
    }

}