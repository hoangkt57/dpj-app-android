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

fun getCurrentTimeUTC(): Long {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.time = Date()
    return calendar.time.time
}

fun localTimeToUTC(date: Long, hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.time = Date(date)
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time.time - TimeZone.getTimeZone("UTC").getOffset(calendar.time.time)
}

fun onlyDateFromTime(date: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.time = Date(date)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time.time
}

fun onlyTimeFromTime(date: Long): Pair<Int, Int> {
    val calendar = Calendar.getInstance()
    calendar.time = Date(date)
    return Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
}

fun timeInSecond(time: Pair<Int, Int>): Int {
    return time.first * 60 * 60 + time.second * 60
}