package com.sonyged.hyperClass.fragment

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sonyged.hyperClass.R
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.databinding.DialogEditBinding
import com.sonyged.hyperClass.databinding.FragmentSettingBinding
import com.sonyged.hyperClass.model.User
import com.sonyged.hyperClass.utils.changePasswordActivity
import com.sonyged.hyperClass.utils.openWeb
import com.sonyged.hyperClass.utils.startLogin
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

        val context = context ?: return

        binding.logout.setOnClickListener {
            viewModel.logout()
            startLogin(context)
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
            openWeb(context, LINK_TERM)
        }

        binding.policy.setOnClickListener {
            openWeb(context, LINK_POLICY)
        }

        binding.license.setOnClickListener {
            openWeb(context, LINK_LICENSES)
        }

        binding.edit1.setOnClickListener {
            editInfoDialog(binding.nameEdit.text.toString(), TYPE_EDIT_NAME)
        }

        binding.edit3.setOnClickListener {
            changePasswordDialog()
        }

        binding.edit4.setOnClickListener {
            editInfoDialog(binding.emailEdit.text.toString(), TYPE_EDIT_EMAIL)
        }

        viewModel.user.observe(viewLifecycleOwner) { updateUser(it) }

    }

    private fun updateUser(user: User) {
        Timber.d("updateUser - user: $user")

        binding.nameEdit.text = user.name
        binding.idEdit.text = user.loginId
        binding.emailEdit.text = user.email
    }

    private fun editInfoDialog(text: String, type: Int) {
        val context = context ?: return
        val editBinding = DialogEditBinding.inflate(LayoutInflater.from(context))
        editBinding.edit.setText(text)
        editBinding.edit.requestFocus()
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(editBinding.root)
            .setNegativeButton(getString(R.string.mtrl_picker_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.mtrl_picker_confirm), null)
            .create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val value = editBinding.edit.text.toString()
            if (value.isEmpty()) {
                editBinding.editField.error = getString(R.string.input_least_1)
                return@setOnClickListener
            }
            if (type == TYPE_EDIT_NAME) {
                viewModel.updateInfo(value, binding.emailEdit.text.toString())
            } else if (type == TYPE_EDIT_EMAIL) {
                viewModel.updateInfo(binding.nameEdit.text.toString(), value)
            }
            dialog.dismiss()
        }
    }

    private fun changePasswordDialog() {
        val context = context ?: return
        changePasswordActivity(context, viewModel.user.value?.id ?: "")
    }
}