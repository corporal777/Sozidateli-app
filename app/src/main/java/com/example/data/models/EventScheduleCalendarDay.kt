package com.example.data.models


data class EventScheduleCalendarDay(
        val millis: Long,
        val week: Int,
        val dayOfWeek: String,
        val dayOfMonth: Int,
        var hasEvents: Boolean
)

data class EventScheduleDay(
        val uniqueId : Int,
        val date : String,
        val millis: Long,
        val dayOfWeek: String?,
        val dayOfMonth: Int,
        var hasEvents: Boolean
){
        override fun equals(other: Any?): Boolean {
                other as EventScheduleDay
                return date == other.date && uniqueId == other.uniqueId
        }

        override fun hashCode(): Int {
                return uniqueId
        }
}