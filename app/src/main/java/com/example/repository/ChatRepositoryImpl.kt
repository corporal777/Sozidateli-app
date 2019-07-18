package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


class ChatRepositoryImpl
@Inject constructor(
        appData: AppData,
        private val api: Api
) : ApiRepository(appData), ChatRepository {

    override fun loadChatList(limit: Int, offset: Int): Maybe<ApiResponse<ChatListResponse>> {
        return api.chatList(limit, offset)
    }

    override fun loadInvitesList(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>> {
        return callPagination(api.chatListInvites(limit, offset))
    }

    override fun loadBannedList(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>> {
        return callPagination(api.chatListBans(limit, offset))
    }

    override fun startChat(userId: Int): Single<ChatStartResponse> {
        return call(api.chatStart(userId))
    }

    override fun uploadImage(chatId: String, image: String): Single<ApiResponseUpload<UploadImage>> {
        return api.uploadChatImage(
                chatId,
                image.let {
                    val imageFile = File(it)
                    val body = RequestBody.create(MediaType.parse("image/*"), imageFile)
                    MultipartBody.Part.createFormData("file[0]", imageFile.name, body)
                })
    }

    override fun getChat(chatId: String): Single<UserChat> {
        return call(api.getChat(chatId))
    }

    override fun searchUser(searchMap: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<User>> {
        return callPagination(api.chatSearch(searchMap, limit, offset))
    }

    override fun chatAccept(chatId: String): Completable {
        return call(api.chatAccept(chatId))
    }

    override fun chatBan(chatId: String): Completable {
        return call(api.chatBan(chatId))
    }

    override fun chatUnban(chatId: String): Completable {
        return call(api.chatUnban(chatId))
    }

    override fun getChatInvitesCount(): Single<ChatInvitesCount> {
        return call(api.getChatInvitesCount())
    }
}