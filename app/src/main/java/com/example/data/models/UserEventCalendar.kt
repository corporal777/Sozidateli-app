package com.example.data.models

data class UserEventCalendar(
        val eventId: Int,
        val eventPlaceGpsLat: Double,
        val eventPlaceGpsLon: Double,
        val time: List<Time>
) {

    data class Time(
            val activity: Int,
            val start: Long,
            val end: Long
    )
}
