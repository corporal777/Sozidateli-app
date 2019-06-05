package com.example.data.models

data class EventRegisterResponse(
        var id: String?,
        var event_id: String?,
        var custom_fields: ArrayList<EventRegisterResponseField>?,
        var event: Event?,
        var group_id: String?,
        var group: Category?,
        var user_id: String?,
        var created: String?,
        var status: String?
)
