package com.sonyged.hyperClass.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.activity.LoginActivity
import com.sonyged.hyperClass.databinding.FragmentSettingBinding
import com.sonyged.hyperClass.model.User
import com.sonyged.hyperClass.viewmodel.MainViewModel
import timber.log.Timber


class SettingPageFragment : BaseFragment(R.layout.fragment_setting) {

    companion object {

        fun create(): Fragment {
            return SettingPageFragment()
        }
    }

    private val binding: FragmentSettingBinding by lazy {
        FragmentSettingBinding.bind(requireView())
    }

    private val viewModel by viewModels<MainViewModel>(ownerProducer = { requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logout.setOnClickListener {
            viewModel.logout()
            openLogin()
            activity?.finish()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.settingLayout.clipToOutline = true
            binding.otherLayout.clipToOutline = true

        }
        binding.settingLayout.setOnClickListener {
            binding.notificationSwitch.toggle()
        }
        binding.term.setOnClickListener {

        }
        binding.policy.setOnClickListener {

        }
        binding.license.setOnClickListener {

        }

        binding.term.setOnClickListener {
            openWeb("https://dist.hyperclass.jp/terms/HyperClass_TOU_v_1_0_0.html")
        }

        binding.policy.setOnClickListener {
            openWeb("https://dist.hyperclass.jp/privacy/HyperClass_PP_v_1_0_0.html")
        }

        binding.license.setOnClickListener {
            openWeb("https://dist.hyperclass.jp/misc/licenses/HyperClassLicense.html")
        }

        viewModel.user.observe(viewLifecycleOwner) { updateUser(it) }

    }

    private fun updateUser(user: User) {
        Timber.d("updateUser - user: $user")

        binding.nameEdit.setText(user.name)
        binding.idEdit.setText(user.loginId)
        binding.passwordEdit.setText(user.password)
        binding.emailEdit.setText(user.email)
    }

    private fun openLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }

    private fun openWeb(url: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}