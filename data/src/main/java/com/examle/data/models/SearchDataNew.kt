package com.examle.data.models

data class SearchDataNew(
    val users: SearchResponseData<UserDetail>,
    val organizations: SearchResponseData<OrganizationNew>,
    val events: SearchResponseData<EventResponse>
)


data class SearchResponseData<T>(
    val data: List<T>,
    val count: Int
)
