package com.sonyged.hyperClass.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sonyged.hyperClass.activity.*
import com.sonyged.hyperClass.constants.*
import com.sonyged.hyperClass.model.*
import timber.log.Timber

private fun startActivityWithException(context: Context, intent: Intent) {
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Timber.e(e, "startActivityWithException")
    }
}

fun startStudentActivity(context: Context, student: Student) {
    val intent = Intent(context, StudentActivity::class.java)
    intent.putExtra(KEY_STUDENT_ID, student.id)
    intent.putExtra(KEY_TITLE, student.name)
    startActivityWithException(context, intent)
}

fun startMainActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    startActivityWithException(context, intent)
}

fun startLogin(context: Context) {
    val intent = Intent(context, LoginActivity::class.java)
    startActivityWithException(context, intent)
}

fun openWeb(context: Context, url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivityWithException(context, browserIntent)
}

fun changePasswordActivity(context: Context, userId: String) {
    val intent = Intent(context, ChangePasswordActivity::class.java)
    intent.putExtra(KEY_USER_ID, userId)
    startActivityWithException(context, intent)
}

fun changePasswordActivityFirst(context: Context, userId: String) {
    val intent = Intent(context, ChangePasswordActivity::class.java)
    intent.putExtra(KEY_USER_ID, userId)
    intent.putExtra(KEY_CHANGE_PASSWORD_FIRST, true)
    startActivityWithException(context, intent)
}

fun startAgreementPpActivity(context: Context, userId: String, changePassword: Boolean) {
    val intent = Intent(context, AgreementPpActivity::class.java)
    intent.putExtra(KEY_USER_ID, userId)
    intent.putExtra(KEY_CHANGE_PASSWORD_FIRST, changePassword)
    startActivityWithException(context, intent)
}

fun previewFileActivity(context: Context, attachment: Attachment) {
    val intent = if (attachment.contentType?.startsWith("image") == true) {
        Intent(context, PreviewImageActivity::class.java)
    } else {
        Intent(context, WebViewActivity::class.java)
    }
    intent.putExtra(KEY_URL, attachment.url)
    startActivityWithException(context, intent)
}

