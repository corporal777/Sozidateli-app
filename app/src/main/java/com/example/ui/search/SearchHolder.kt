package com.example.ui.search

import com.example.data.models.SearchTypeEvent

data class SearchHolder(
        var text: String? = null,
        var date: Long = 0,
        var dateFrom: Long = 0,
        var dateTo: Long = 0,
        var places: ArrayList<SearchTypeEvent> = ArrayList(),
        var typeEvents: ArrayList<SearchTypeEvent> = ArrayList()
)