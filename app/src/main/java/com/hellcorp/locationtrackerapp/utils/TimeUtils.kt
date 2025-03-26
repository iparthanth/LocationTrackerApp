package com.hellcorp.locationtrackerapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object TimeUtils {
    @SuppressLint("SimpleDateFormat")
    private val timeFormatter = SimpleDateFormat("HH:mm:ss:SS")
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")

    fun getTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        timeFormatter.timeZone = TimeZone.getTimeZone("UTC") // Форматирует начало отчета в 00:00:00
        calendar.timeInMillis = timestamp
        return timeFormatter.format(calendar.time)
    }

    fun getCurrentDate() : String {
        val cv = Calendar.getInstance()
        return dateFormat.format(cv.time)
    }
}