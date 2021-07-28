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

fun formatDateTimeToLong(dueDate: String?): Long {
    try {
        if (dueDate.isNullOrEmpty()) {
            return 0
        }
        val serverDf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        serverDf.timeZone = TimeZone.getTimeZone("UTC")
        val date = serverDf.parse(dueDate) ?: Date()
        return date.time
    } catch (e: Exception) {
        Timber.e(e)
    }
    return 0
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

fun formatDate2(dateLong: Long): String {
    try {
        val date = Date(dateLong)
        val clientDf = SimpleDateFormat("yy/MM/dd(E) HH:mm", Locale.JAPAN)
        return clientDf.format(date)
    } catch (e: Exception) {
        Timber.e(e)
    }
    return ""
}

fun formatTime(dateLong: Long): String {
    try {
        val date = Date(dateLong)
        val clientDf = SimpleDateFormat("HH:mm", Locale.JAPAN)
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

fun formatDayWithName(dateLong: Long): String {
    try {
        val date = Date(dateLong)
        val clientDf = SimpleDateFormat("dd (E)", Locale.JAPAN)
        return clientDf.format(date)
    } catch (e: Exception) {
        Timber.e(e)
    }
    return ""
}

fun formatServerTime(dateLong: Long): String {
    try {
        val date = Date(dateLong)
        val clientDf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.US)
        clientDf.timeZone = TimeZone.getTimeZone("UTC")
        return clientDf.format(date)
    } catch (e: Exception) {
        Timber.e(e, "formatServerTime")
    }
    return ""
}

fun diffDate(date1: Long, date2: Long): Int {
    val diff = date2 - date1
    return (diff / (24 * 60 * 60 * 1000)).toInt()
}