package com.examle.domain.model

data class PaginationResponse<T>(
    val totalCount: Int?,
    val data: List<T>
){
    fun isEmptyData() = data.isEmpty() && totalCount == 0
}

data class NotificationsResponse<T>(
    val totalCount: Int?,
    val data: List<T>,
    val totalUnreadInvites : Int?,
    val totalUnread : Int?,
    val allUnread : Int?,
    val activeInvites: Int?,
    val archiveInvites: Int?,
)