package com.example.repository

import android.graphics.Bitmap
import com.example.data.bodies.CreateChatBody
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.Body

interface ChatRepository {

    //fun loadChatList(limit: Int, offset: Int): Maybe<ApiResponse<ChatListResponse>>

    //fun loadInvitesList(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>

    //fun loadBannedList(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>

    //fun startChat(userId: String): Single<ChatStartResponse>

    fun uploadImage(chatId: String, bitmap: Bitmap): Single<ApiResponseUpload<UploadImage>>

    //fun getChat(chatId: String): Single<UserChat>

    //fun searchUser(limit: Int, offset: Int): Maybe<PaginationResponse<User?>>

    //fun chatAccept(chatId: String): Completable

    //fun chatBan(chatId: String): Completable

    //fun chatUnban(chatId: String): Completable

    //New API
    fun getChats(map: Map<String, Any>): Maybe<ApiNewResponse<List<ChatModel>>>

    fun getChatById(chatId: String, map: Map<String, Any>): Single<ChatModel>

    fun createChat(body: CreateChatBody): Single<CreatedChatModel>

    fun bannedList(map: Map<String, Any>): Maybe<PaginationResponse<UserChat>>

    fun chatBann(body: CreateChatBody): Single<BannedUsersModel>

    fun deleteBan(id: Int): Completable

    fun acceptChat(id: Int): Completable

    fun getChatInvitesCount(): Single<ChatInvitesCount>

    fun sendChatMessage(body: RequestBody): Single<MessageModel>
}