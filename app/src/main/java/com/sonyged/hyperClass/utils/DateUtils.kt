package com.sonyged.hyperClass.utils

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun formatDateTime(dueDate: String): String {
    try {
        val serverDf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        serverDf.timeZone = TimeZone.getTimeZone("UTC")
        val date = serverDf.parse(dueDate) ?: Date()
        val clientDf = SimpleDateFormat("yy/MM/dd(E) HH:mm", Locale.JAPAN)
        return clientDf.format(date)
    } catch (e: Exception) {
        Timber.e(e)
    }
    return ""
}

fun formatDate(dateLong: Long): String {
    try {
        val date = Date(dateLong)
        val clientDf = SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN)
        return clientDf.format(date)
    } catch (e: Exception) {
        Timber.e(e)
    }
    return ""
}

fun formatDate1(dateLong: Long): String {
    try {
        val date = Date(dateLong)
        val clientDf = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
        return clientDf.format(date)
    } catch (e: Exception) {
        Timber.e(e)
    }
    return ""
}