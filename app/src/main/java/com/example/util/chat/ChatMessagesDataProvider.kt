package com.example.util.chat

import ru.houseofapps.chat.ChatRepository
import ru.houseofapps.chat.SocketRepository

object ChatMessagesDataProvider {

    private val chatDataMap = mutableMapOf<String, ChatMessagesData>()

    fun provideFor(chatId: String, socketRepository: SocketRepository, chatRepository: ChatRepository): ChatMessagesData {
        var chatMessagesData = chatDataMap[chatId]
        if (chatMessagesData == null) {
            chatMessagesData = ChatMessagesData(chatId, socketRepository, chatRepository)
            chatDataMap[chatId] = chatMessagesData
        }

        return chatMessagesData
    }
}