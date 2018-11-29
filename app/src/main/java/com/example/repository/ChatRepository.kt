package com.example.repository

import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.google.firebase.firestore.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

interface ChatRepository {

    fun subscribeOnChatList(): Flowable<List<UserChat>>

    fun getChatId(): String

    fun getChatMessageQuery(chatId: String): Query

    fun getChatListQuery(): Query

    fun sendChatMessage(chatId: String, toUser: String, message: ChatMessage): Completable
}