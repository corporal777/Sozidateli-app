package com.example.util.pagination

data class PaginationResponse<T>(
    val totalCount: Int?,
    val data: List<T>
)

data class NotificationsResponse<T>(
    val totalCount: Int?,
    val data: List<T>,
    val totalUnreadInvites : Int?,
    val totalUnread : Int?
)