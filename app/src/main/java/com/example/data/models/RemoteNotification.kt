package com.example.data.models

data class RemoteNotification(
        val id: Int,
        val user_id: Int,
        val organization_id: Int,
        val assistance_id: Int,
        val project_id: Int,
        val event_id: Int?,
        val code: String?,
        val type: String,
        val time: String,
        val text: String?,
        val status: String?,
        val extra: String?
) {
    companion object {
        const val TYPE_RATE = "Оценка мероприятия"
        const val TYPE_INVITE = "Приглашение"
        const val TYPE_NOTIFICATION = "Уведомление"

        const val STATUS_NONE = "none"
        const val STATUS_ACKNOWLEDGED = "acknowledged"
        const val STATUS_ACCEPTED = "accepted"
        const val STATUS_DECLINED = "declined"
        const val STATUS_CANCELLED = "cancelled"
    }
}