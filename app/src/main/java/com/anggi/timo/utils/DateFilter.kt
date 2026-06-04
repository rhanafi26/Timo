package com.anggi.timo.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFilter {

    fun isToday(date: String): Boolean {

        val today =
            SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            ).format(Date())

        return date.startsWith(today)
    }

    fun isThisWeek(date: String): Boolean {

        val sdf =
            SimpleDateFormat(
                "dd-MM-yyyy HH:mm",
                Locale.getDefault()
            )

        val calendar = Calendar.getInstance()

        val currentWeek =
            calendar.get(Calendar.WEEK_OF_YEAR)

        val currentYear =
            calendar.get(Calendar.YEAR)

        val studyDate = sdf.parse(date)

        calendar.time = studyDate!!

        return currentWeek ==
                calendar.get(Calendar.WEEK_OF_YEAR)
                &&
                currentYear ==
                calendar.get(Calendar.YEAR)
    }

    fun isThisYear(date: String): Boolean {

        val sdf =
            SimpleDateFormat(
                "dd-MM-yyyy HH:mm",
                Locale.getDefault()
            )

        val studyDate = sdf.parse(date)

        val cal = Calendar.getInstance()

        cal.time = studyDate!!

        return cal.get(Calendar.YEAR) ==
                Calendar.getInstance()
                    .get(Calendar.YEAR)
    }
}