package com.example.data.models

import java.util.*

data class LocalNotification(
        var chatId: String? = null,
        var senderId: Int = 0,
        var userName: String? = null,
        var text: String? = null,
        var sendAt: Date? = null,
        var avatar: String? = null,
        @field:JvmField
        var isShowed: Boolean = false,
        var messageId: String? = null
)