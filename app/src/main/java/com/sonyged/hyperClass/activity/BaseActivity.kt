package com.sonyged.hyperClass.activity

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sonyged.hyperClass.R

abstract class BaseActivity : AppCompatActivity() {

    private var progressDialog: AlertDialog? = null

    protected fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = MaterialAlertDialogBuilder(this, R.style.ProgressDialog)
                .setView(R.layout.dialog_loading)
                .setCancelable(false)
                .create()
            progressDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent);
        }
        progressDialog?.show()
    }

    protected fun hideProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

}