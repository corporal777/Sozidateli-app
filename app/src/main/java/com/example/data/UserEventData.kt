package com.example.data

import com.example.data.models.*

class UserEventData {

    var event: Event? = null
    var days: List<EventScheduleCalendarDay>? = null
    var tags: List<EventTag>? = null
    var categories: List<EventCategory>? = null
    var mapInfo:MapInfo?=null

    var isStaticDataLoaded = false

    fun clear() {
        event = null
        days = null
        tags = null
        categories = null
        isStaticDataLoaded = false
        mapInfo = null
    }
}