package com.anggi.timo.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateRangeUtils {

    private val sdf =
        SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
        )

    fun getToday(): String {
        return sdf.format(Calendar.getInstance().time)
    }

    fun getLast7Days(): String {

        val endDate =
            Calendar.getInstance()

        val startDate =
            Calendar.getInstance()

        startDate.add(
            Calendar.DAY_OF_MONTH,
            -7
        )

        return "${sdf.format(startDate.time)} - ${
            sdf.format(endDate.time)
        }"
    }

    fun getLast30Days(): String {

        val endDate =
            Calendar.getInstance()

        val startDate =
            Calendar.getInstance()

        startDate.add(
            Calendar.DAY_OF_MONTH,
            -30
        )

        return "${sdf.format(startDate.time)} - ${
            sdf.format(endDate.time)
        }"
    }
}