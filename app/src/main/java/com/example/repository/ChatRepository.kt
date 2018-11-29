package com.example.repository

import com.example.data.models.ChatMessage
import com.google.firebase.firestore.Query
import io.reactivex.Flowable

interface ChatRepository {

    fun subscribeChatMessages(chatId: String): Flowable<List<ChatMessage>>

    fun getChatMessageQuery(chatId: String): Query
}