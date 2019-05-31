package com.example.data.models

import ru.houseofapps.chat.models.Message

class UserChatMessage(
        val message: Message,
        val isMyMessage: Boolean,
        val isUnreadLabel:Boolean
)