package com.example.util

object TimerFormatter {
    private const val MINUTES_TIME_FORMAT = "%d:%02d"

    fun formatMinutes(time: Long): String {
        val minutes = time / 60000
        val seconds = (time / 1000) % 60

        return String.format(MINUTES_TIME_FORMAT, minutes, seconds)
    }
}