package com.example.repository

import com.example.data.models.ApiResponseUpload
import com.example.data.models.ChatStartResponse
import com.example.data.models.UploadImage
import com.example.data.models.UserChat
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Single

interface ChatRepository {

    fun loadChatList(searchMap: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>

    fun startChat(userId: Int): Single<ChatStartResponse>

    fun uploadImage(chatId: String, image: String): Single<ApiResponseUpload<UploadImage>>

    fun getChat(chatId: String): Single<UserChat>

    fun searchUser(searchMap: Map<String, Any>, limit: Int, offset: Int): Maybe<PaginationResponse<User>>
}