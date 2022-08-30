package com.example.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.api.Api
import com.example.api.NewApi
import com.example.data.AppData
import com.example.data.bodies.CreateChatBody
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.ui.chat.body.MessageBodyNew
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.RequestBody
import toBodyPart
import javax.inject.Inject


class ChatRepositoryImpl
@Inject constructor(
        private val appData: AppData,
        private val api: Api,
        private val newApi: NewApi
) : ApiRepository(appData), ChatRepository {

    /*override fun loadChatList(limit: Int, offset: Int): Maybe<ApiResponse<ChatListResponse>> {
        return api.chatList(limit, offset)
    }

    override fun loadInvitesList(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>> {
        return callPagination(api.chatListInvites(limit, offset))
    }

    override fun loadBannedList(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>> {
        return callPagination(api.chatListBans(limit, offset))
    }

    override fun startChat(userId: String): Single<ChatStartResponse> {
        return call(api.chatStart(userId))
    }*/

    override fun uploadImage(chatId: String, bitmap: Bitmap): Single<ApiResponseUpload<UploadImage>> {
        return api.uploadChatImage(
                chatId,
                bitmap.toBodyPart("file[0]", "image.png")
        )
    }

    /*override fun getChat(chatId: String): Single<UserChat> {
        return call(api.getChat(chatId))
    }

    override fun searchUser(limit: Int, offset: Int): Maybe<PaginationResponse<User?>> {
        return callPagination(api.chatSearch(emptyMap(), limit, offset))
    }

    override fun chatAccept(chatId: String): Completable {
        return call(api.chatAccept(chatId))
    }

    override fun chatBan(chatId: String): Completable {
        return call(api.chatBan(chatId))
    }

    override fun chatUnban(chatId: String): Completable {
        return call(api.chatUnban(chatId))
    }*/

    override fun getChats(map: Map<String, Any>): Maybe<ApiNewResponse<List<ChatModel>>> =
            newApi.getChats(map)

    override fun getChatById(chatId: String, map: Map<String, Any>): Single<ChatModel> =
            newApi.getChatById(chatId, map)

    override fun createChat(body: CreateChatBody): Single<CreatedChatModel> =
            newApi.createChat(body)

    override fun bannedList(map: Map<String, Any>): Maybe<PaginationResponse<UserChat>> =
            newApi.bannedList(map).map {
                val result = mutableListOf<UserChat>()
                it.data.forEach { chat ->
                    result.add(UserChat(chat.id, chat.binds?.user!!,
                            chat.createdDate?: "", chat.binds.lastUnreadMessage?.message, chat.binds.lastUnreadMessage?.createdDate,
                            if (chat.binds.lastUnreadMessage?.file == null) Message.MessageType.TEXT else Message.MessageType.IMAGE,
                            chat.binds.lastUnreadMessage?.acknowledge?.get(0)?.user, null, chat.binds.lastUnreadMessage?.id.toString(),
                            false, false, false, false, false, false,
                            chat.binds.event?.id.toString(), 0))
                }
                PaginationResponse(it.totalCount, result)
            }

    override fun chatBann(body: CreateChatBody): Single<BannedUsersModel> =
            newApi.chatBan(body)

    override fun deleteBan(id: Int): Completable =
            newApi.deleteBan(id)

    override fun acceptChat(id: Int): Completable =
            newApi.acceptChat(id)

    override fun getChatInvitesCount(): Single<ChatInvitesCount> =
            newApi.getChatsCount(mapOf(ChatModel.CHAT_SORT to "desc", ChatModel.CHAT_LIMIT to 1, ChatModel.CHAT_OFFSET to 0,
                    ChatModel./*CHAT_INVITED_USER_STATUS*/CHAT_USER_STATUS to "pending", ChatModel.CHAT_INVITED_USER to appData.getId(),
                    ChatModel.CHAT_USER to appData.getId())).map {
                ChatInvitesCount(it.totalCount?: 0)
            }

    override fun sendChatMessage(body: RequestBody): Single<MessageModel> {
        return newApi.sendChatMessage(body)
    }

    override fun sendChatMessageNew(body: MessageBodyNew): Single<MessageModel> {
        return newApi.sendChatMessageNew(body)
    }

    override fun getChatMessages(map: Map<String, Any>): Single<ApiNewResponse<List<MessageModel>>> =
            newApi.getChatMessages(map)

    override fun markMessageAsRead(id: Int): Completable =
            newApi.markMessageAsRead(id)
}