package com.example.repository

import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.example.util.pagination.PaginationResponse
import com.google.firebase.firestore.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface ChatRepository {

    fun getChatMessageQuery(chatId: String): Query

    fun sendChatMessage(chatId: String, userId: String, message: ChatMessage): Completable

    fun singInFirebase(): Completable

    fun loadChatList(searchMap: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>

    fun setMessageRead(chatId: String, messageId: String): Completable

    fun subscribeChatUnreadMessageCount(): Flowable<Int>
}