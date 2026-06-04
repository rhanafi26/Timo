package com.anggi.timo.utils

object ProgressUtils {

    fun hitungPersentase(progress: Int, target: Int): String {

        if (target <= 0) return "0%"

        val persen = (progress.toDouble() / target.toDouble()) * 100

        return "${persen.toInt().coerceAtMost(100)}%"
    }
    fun timeToSeconds(time: String): Int {
        val parts = time.split(":")
        val minutes = parts[0].toInt()
        val seconds = parts[1].toInt()

        return (minutes * 60) + seconds
    }

    fun formatDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return String.format(
            "%02d:%02d:%02d",
            hours,
            minutes,
            secs
        )
    }

    fun hitungTingkatFokus(
        totalStudyTime: Int,
        totalBreaks: Int = 0
    ): String {
        // Calculate total 20-minute sessions
        val sesi20Menit = totalStudyTime / 1200

        // If there are breaks, calculate break frequency per 20 minutes
        return if (sesi20Menit > 0 && totalBreaks > 0) {
            val breaksPerSession = totalBreaks / sesi20Menit

            when {
                breaksPerSession <= 5 -> "A"
                breaksPerSession <= 10 -> "B"
                breaksPerSession <= 15 -> "C"
                breaksPerSession <= 20 -> "D"
                else -> "E"
            }
        } else if (sesi20Menit > 0) {
            // No breaks = perfect focus
            "A+"
        } else {
            "D"
        }
    }

}