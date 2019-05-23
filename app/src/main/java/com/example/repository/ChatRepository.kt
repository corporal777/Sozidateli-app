package com.example.repository

import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import com.google.firebase.firestore.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.houseofapps.chat.models.Message
import ru.houseofapps.chat.models.MessageResponse

interface ChatRepository {

    fun connect(userId: String): Flowable<Any>

    fun joinChat(chatId: String, users: Array<String>): Completable

    fun loadChatMessages(chatId: String, startAfter: String, limit: Int): Maybe<MessageResponse>

    fun subscribeNewMessage(): Flowable<Message>

    fun sendChatMessage(chatId: String, message: String, type: String): Completable


    fun getChatMessageQuery(chatId: String): Query

    fun loadChatList(searchMap: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>

    fun setMessagesRead(chatId: String, ids: List<String>): Completable

    fun subscribeChatUnreadMessageCount(): Flowable<Int>

    fun subscribeChatUnreadMessageCount(chatId: String): Flowable<Int>

    fun loadChatLastMessage(): Maybe<LocalNotification>

    fun subscribeChatLastMessage(): Flowable<LocalNotification>

    fun startChat(userId: Int): Single<ChatStartResponse>

    fun uploadImage(chatId: String, image: String): Single<ApiResponseUpload<UploadImage>>

    fun getChat(chatId: String): Single<UserChat>

    fun getMessage(chatId: String, messageId: String): Maybe<ChatMessage>

    fun setMessageShowed(userId: String?, chatId: String, messageId: String): Completable

    fun searchUser(name: String, email: String, limit: Int, offset: Int): Maybe<PaginationResponse<User>>
}