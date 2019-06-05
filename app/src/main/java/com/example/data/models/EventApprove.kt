package com.example.data.models

data class EventApprove(
        val event: Event,
        val status: Status
) {
    enum class Status(val code: String) {
        EMPTY("empty"),
        WAIT_VERIFY_USER("wait_verify_user"),
        PENDING("pending"),
        APPROVED("approved"),
        DECLINED("declined")
    }
}

fun String?.toEventApproveStatus(): EventApprove.Status {
    return when (this) {
        EventApprove.Status.WAIT_VERIFY_USER.code -> EventApprove.Status.WAIT_VERIFY_USER
        EventApprove.Status.PENDING.code -> EventApprove.Status.PENDING
        EventApprove.Status.APPROVED.code -> EventApprove.Status.APPROVED
        EventApprove.Status.DECLINED.code -> EventApprove.Status.DECLINED
        else -> EventApprove.Status.EMPTY
    }
}

