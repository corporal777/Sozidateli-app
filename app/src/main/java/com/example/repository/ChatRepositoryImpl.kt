package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.ApiResponseUpload
import com.example.data.models.ChatStartResponse
import com.example.data.models.UploadImage
import com.example.data.models.UserChat
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
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

    override fun loadChatList(searchMap: Map<String, Any>, limit: Int, offset: Int) = callPagination(api.chatList(searchMap, limit, offset))

    override fun startChat(userId: Int): Single<ChatStartResponse> {
        return call(api.startChat(userId))
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

    override fun searchUser(name: String, email: String, limit: Int, offset: Int): Maybe<PaginationResponse<User>> {
        return callPagination(api.chatSearch(if (name.isEmpty()) " " else name, limit, offset))
    }
}