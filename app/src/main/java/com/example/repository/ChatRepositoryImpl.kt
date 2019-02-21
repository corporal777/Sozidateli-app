package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.ApiResponseUpload
import com.example.data.models.ChatMessage
import com.example.data.models.ChatStartResponse
import com.example.data.models.UploadImage
import com.example.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirebaseAuth
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


class ChatRepositoryImpl
@Inject constructor(
        private val firestore: FirebaseFirestore,
        private val firebaseAuth: FirebaseAuth,
        private val appData: AppData,
        private val api: Api
) : ApiRepository(appData), ChatRepository {

    override fun getChatMessageQuery(chatId: String) = firestore.collection(COLLECTION_CHATS)
            .document(chatId)
            .collection(COLLECTION_MESSAGES)
            .orderBy(FIELD_SEND_AT)

    override fun sendChatMessage(chatId: String, userId: String, message: ChatMessage): Completable {
        return RxFirestore.runTransaction(firestore) {
            val unreadMessageCountRef = firestore.collection(COLLECTION_USERS).document(userId)
            val userUnreadMessageCount = it.get(unreadMessageCountRef).getDouble(FIELD_UNREAD_MESSAGE_COUNT)
            if (userUnreadMessageCount == null) {
                it.set(unreadMessageCountRef, mapOf(FIELD_UNREAD_MESSAGE_COUNT to 1))
            } else {
                it.update(unreadMessageCountRef, FIELD_UNREAD_MESSAGE_COUNT, userUnreadMessageCount.plus(1))
            }
            it.set(firestore.collection(COLLECTION_CHATS)
                    .document(chatId)
                    .collection(COLLECTION_MESSAGES)
                    .document(), message.toMap())
            null
        }.andThen(call(api.chatLastMessage(chatId, message.text ?: "")))
    }

    override fun setMessageRead(chatId: String, messageId: String): Completable {
        return RxFirestore.runTransaction(firestore) {
            val messageRef = firestore.collection(COLLECTION_CHATS).document(chatId)
                    .collection(COLLECTION_MESSAGES).document(messageId)
            val message = it.get(messageRef)

            if (message.getBoolean(FIELD_IS_READ) == true) return@runTransaction null
            it.update(messageRef, FIELD_IS_READ, true)

            val unreadMessageCountRef = firestore.collection(COLLECTION_USERS).document(appData.getUser().user_id.toString())
            val userUnreadMessageCount = it.get(unreadMessageCountRef).getDouble(FIELD_UNREAD_MESSAGE_COUNT)
                    ?: 0.0

            if (userUnreadMessageCount > 0) {
                it.update(unreadMessageCountRef, FIELD_UNREAD_MESSAGE_COUNT, userUnreadMessageCount.minus(1))
            }
            null
        }
    }

    override fun singInFirebase(): Completable {
        return RxFirebaseAuth.signInAnonymously(firebaseAuth).flatMapCompletable { Completable.complete() }
    }

    override fun loadChatList(searchMap: Map<String, Any>, limit: Int, offset: Int) = callPagination(api.chatSearch(searchMap, limit, offset))

    override fun subscribeChatUnreadMessageCount(): Flowable<Int> {
        val unreadMessageCountRef = firestore.collection(COLLECTION_USERS).document(appData.getUser().user_id.toString())
        return RxFirestore.observeDocumentRef(unreadMessageCountRef)
                .map { it.getDouble(FIELD_UNREAD_MESSAGE_COUNT)?.toInt() ?: 0 }
    }

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
}