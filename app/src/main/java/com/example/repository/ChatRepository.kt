package com.example.repository

import com.example.data.models.ChatMessage
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Flowable

interface ChatRepository {

    fun subscribeChatMessages(chatId: String): Flowable<List<ChatMessage>>
}