package com.example.data.models

data class Event(
        val id: String,
        val name: String,
        val logo: String,
        val startDate: Long,
        val finishDate: Long,
        val organizationName: String,
        val status: Status
)

enum class Status(val code: Int) {
    APPROVED(1),
    CONFIRMATION_EXPECTED(2),
    FINISHED(3)
}