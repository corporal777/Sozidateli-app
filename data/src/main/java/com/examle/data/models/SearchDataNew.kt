package com.examle.data.models

import com.examle.data.models.event.EventResponse

data class SearchDataNew(
    val users: SearchResponseData<UserDetail>,
    val organizations: SearchResponseData<OrganizationNew>,
    val events: SearchResponseData<EventResponse>
)


data class SearchResponseData<T>(
    val data: List<T>,
    val count: Int
)
