package com.example.data

import com.example.data.models.*

class UserEventData {

    var event: Event? = null
    var days: List<EventScheduleCalendarDay>? = null
    var tags: List<EventTag>? = null
    var categories: List<EventCategory>? = null
    var mapInfo: MapInfo? = null
    var partners: List<EventParther>? = null
    var subEvents: List<SubEvent>? = null

    var isStaticDataLoaded = false

    var isDataFromLocalStorage = false
    var dataLoadingDate = 0L

    fun clear() {
        event = null
        days = null
        tags = null
        categories = null
        isStaticDataLoaded = false
        isDataFromLocalStorage = false
        dataLoadingDate = 0L
        mapInfo = null
        partners = null
        subEvents = null
    }
}