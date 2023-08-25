package com.example.data.models

data class SearchDataNew(
    val users: SearchResponseData<UserDetail>,
    val organizations: SearchResponseData<OrganizationNew>,
    val events: SearchResponseData<EventNew>
)


data class SearchResponseData<T>(
    val data: List<T>,
    val count: Int
)
