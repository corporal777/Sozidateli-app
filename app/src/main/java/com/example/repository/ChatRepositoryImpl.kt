package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.pagination.PaginationResponse
import com.google.firebase.firestore.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.houseofapps.chat.SocketRepository
import ru.houseofapps.chat.models.MessageResponse
import java.io.File
import javax.inject.Inject


class ChatRepositoryImpl
@Inject constructor(
        private val appData: AppData,
        private val api: Api,
        private val socketRepository: SocketRepository,
        private val chatRepository: ru.houseofapps.chat.ChatRepository
) : ApiRepository(appData), ChatRepository {

    override fun connect(userId: String) = socketRepository.connect(userId)

    override fun joinChat(chatId: String, users: Array<String>) = socketRepository.joinToRoom(chatId, users)

    override fun loadChatMessages(chatId: String, startAfter: String, limit: Int): Maybe<MessageResponse> {
        return chatRepository.getMessages(chatId, limit, startAfter).toMaybe()
    }

    override fun subscribeNewMessage() = socketRepository.subscribeToMessageInRoom()

    override fun sendChatMessage(chatId: String, message: String, type: String): Completable {
        return socketRepository.sendMessage(type, message)
                .flatMapCompletable { call(api.chatLastMessage(chatId, message, it._id)) }
    }

    override fun getChatMessageQuery(chatId: String): Query {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setMessagesRead(chatId: String, ids: List<String>): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeChatUnreadMessageCount(): Flowable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeChatUnreadMessageCount(chatId: String): Flowable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadChatLastMessage(): Maybe<LocalNotification> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeChatLastMessage(): Flowable<LocalNotification> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMessage(chatId: String, messageId: String): Maybe<ChatMessage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setMessageShowed(userId: String?, chatId: String, messageId: String): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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