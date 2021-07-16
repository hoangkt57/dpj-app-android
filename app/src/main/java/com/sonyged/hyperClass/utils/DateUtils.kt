package com.sonyged.hyperClass.utils

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun formatDateTime(dueDate: String?): String {
    try {
        if (dueDate.isNullOrEmpty()) {
            return ""
        }
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

fun range7DayFromCurrent(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    val time1 = calendar.time.time
    calendar.add(Calendar.DATE, 7)
    val time2 = calendar.time.time
    return Pair(time1, time2)
}