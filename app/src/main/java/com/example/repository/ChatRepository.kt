package com.example.repository

import com.example.data.models.*
import com.example.util.pagination.PaginationResponse
import com.google.firebase.firestore.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface ChatRepository {

    fun getChatMessageQuery(chatId: String): Query

    fun sendChatMessage(chatId: String, userId: String, message: ChatMessage): Completable

    fun singInFirebase(): Completable

    fun loadChatList(searchMap: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>

    fun setMessagesRead(chatId: String, ids: List<String>): Completable

    fun subscribeChatUnreadMessageCount(): Flowable<Int>

    fun subscribeChatUnreadMessageCount(chatId: String): Flowable<Int>

    fun subscribeChatLastMessage(): Flowable<LocalNotification>

    fun startChat(userId: Int): Single<ChatStartResponse>

    fun uploadImage(chatId: String, image: String): Single<ApiResponseUpload<UploadImage>>

    fun getChat(chatId: String): Single<UserChat>

    fun getMessage(chatId: String, messageId: String): Maybe<ChatMessage>

    fun setMessageShowed(userId: String?, chatId: String, messageId: String): Completable
}