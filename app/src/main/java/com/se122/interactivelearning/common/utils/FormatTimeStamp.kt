package com.se122.interactivelearning.common.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatTimeStamp(timestampMillis: Long): String {
    val now = System.currentTimeMillis()

    val calendarNow = Calendar.getInstance().apply { timeInMillis = now }
    val calendarMsg = Calendar.getInstance().apply { timeInMillis = timestampMillis }

    val sameDay = calendarNow.get(Calendar.YEAR) == calendarMsg.get(Calendar.YEAR) &&
            calendarNow.get(Calendar.DAY_OF_YEAR) == calendarMsg.get(Calendar.DAY_OF_YEAR)

    val dateFormat = if (sameDay) {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    } else {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    }

    return dateFormat.format(Date(timestampMillis))
}