package com.example.events

import com.example.data.models.SearchTypeEvent

data class OnAddSearchTypeEvent(
        var data: MutableList<SearchTypeEvent>,
        var isOrganization:Boolean
)