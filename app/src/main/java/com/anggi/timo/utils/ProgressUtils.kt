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
        totalBreakTime: Int
    ): String {
        println("Total Study: $totalStudyTime")
        println("totalBreakTime: $totalBreakTime")

        // minimal 1 blok (0-20 menit)
        val blok20Menit = maxOf(1, (totalStudyTime - 1) / 1200 + 1)

        val batas = blok20Menit * 5

        return when {
            totalBreakTime <= batas -> "A"
            totalBreakTime <= batas * 2 -> "B"
            totalBreakTime <= batas * 3 -> "C"
            else -> "D"
        }
    }

    fun focusToScore(focus: String): Int {
        return when (focus) {
            "A" -> 5
            "AB" -> 4
            "B" -> 3
            "BC" -> 2
            "C" -> 1
            else -> 0
        }
    }

    fun scoreToFocus(score: Int): String {
        return when (score) {
            5 -> "A"
            4 -> "AB"
            3 -> "B"
            2 -> "BC"
            else -> "C"
        }
    }
    fun calculateAverageFocus(focusList: List<String>): String {
        if (focusList.isEmpty()) return "-"

        val totalScore = focusList.sumOf {
            focusToScore(it)
        }

        val average = totalScore / focusList.size

        return scoreToFocus(average)
    }

}