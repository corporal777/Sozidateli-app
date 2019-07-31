package com.example.repository

import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface ChatRepository {

    fun loadChatList(limit: Int, offset: Int): Maybe<ApiResponse<ChatListResponse>>

    fun loadInvitesList(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>

    fun loadBannedList(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>

    fun startChat(userId: String): Single<ChatStartResponse>

    fun uploadImage(chatId: String, image: String): Single<ApiResponseUpload<UploadImage>>

    fun getChat(chatId: String): Single<UserChat>

    fun searchUser(searchMap: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<User>>

    fun chatAccept(chatId: String): Completable

    fun chatBan(chatId: String): Completable

    fun chatUnban(chatId: String): Completable

    fun getChatInvitesCount(): Single<ChatInvitesCount>
}