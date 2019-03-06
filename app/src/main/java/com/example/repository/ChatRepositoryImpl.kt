package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.*
import com.example.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
            .orderBy(FIELD_SEND_AT, Query.Direction.DESCENDING)

    override fun sendChatMessage(chatId: String, userId: String, message: ChatMessage): Completable {
        val refMsg = firestore.collection(COLLECTION_CHATS)
                .document(chatId)
                .collection(COLLECTION_MESSAGES)
                .document()

        val messageId = refMsg.id

        return RxFirestore.runTransaction(firestore) {
            it.set(refMsg, message.toMap())
        }
                .andThen(call(api.chatLastMessage(chatId, message.text ?: "", messageId)))
                .andThen(
                        RxFirestore.runTransaction(firestore) {
                            val lastMsgRef = firestore.collection(COLLECTION_USERS).document(userId)
                            val userUnreadMessageCount = it.get(lastMsgRef).getDouble(FIELD_UNREAD_MESSAGE_COUNT)
                                    ?: 0.0

                            val unreadChatMessageCountRef = firestore.collection(COLLECTION_CHATS).document(chatId).collection(COLLECTION_USERS).document(userId)
                            val userUnreadChatMessageCount = it.get(unreadChatMessageCountRef).getDouble(FIELD_UNREAD_MESSAGE_COUNT)


                            if (userUnreadChatMessageCount == null) {
                                it.set(unreadChatMessageCountRef, mapOf(FIELD_UNREAD_MESSAGE_COUNT to 1))
                            } else {
                                it.update(unreadChatMessageCountRef, FIELD_UNREAD_MESSAGE_COUNT, userUnreadChatMessageCount.plus(1))
                            }

                            val lastMessage = mapOf(
                                    FIELD_CHAT_ID to chatId,
                                    FIELD_SENDER_ID to appData.getUser().user_id,
                                    FIELD_USER_NAME to appData.getUser().fullName,
                                    FIELD_TEXT to message.text,
                                    FIELD_SEND_AT to message.sendAt,
                                    FIELD_AVATAR to appData.getUser().user_avatar,
                                    FIELD_IS_SHOWED to false,
                                    FIELD_UNREAD_MESSAGE_COUNT to userUnreadMessageCount.plus(1),
                                    FIELD_MESSAGE_ID to messageId
                            )

                            it.set(lastMsgRef, lastMessage)
                            null
                        }
                )
    }

    override fun setLastMessageShowed(chatId: String?, messageId: String?): Completable {
        return RxFirestore.runTransaction(firestore) {
            val lastMsgRef = firestore.collection(COLLECTION_USERS).document(appData.getUser().user_id.toString())
            var msgRef: DocumentReference? = null

            if (chatId != null && messageId != null) {
                msgRef = firestore.collection(COLLECTION_CHATS)
                        .document(chatId)
                        .collection(COLLECTION_MESSAGES)
                        .document(messageId)
            }

            it.update(lastMsgRef, mapOf(
                    FIELD_IS_SHOWED to true
            ))

            msgRef?.let { doc ->
                it.update(doc, mapOf(
                        FIELD_IS_SHOWED to true
                ))
            }
            null
        }
    }

    override fun setMessagesRead(chatId: String, ids: List<String>): Completable {
        return RxFirestore.runTransaction(firestore) { transaction ->
            val messageCollectionRef = firestore.collection(COLLECTION_CHATS)
                    .document(chatId)
                    .collection(COLLECTION_MESSAGES)

            val unreadMessageCountRef = firestore.collection(COLLECTION_USERS).document(appData.getUser().user_id.toString())
            val userUnreadMessageCount = transaction.get(unreadMessageCountRef).getDouble(FIELD_UNREAD_MESSAGE_COUNT)
                    ?: 0.0

            val unreadChatMessageCountRef = firestore.collection(COLLECTION_CHATS).document(chatId).collection(COLLECTION_USERS).document(appData.getUser().user_id.toString())
            val userUnreadChatMessageCount = transaction.get(unreadMessageCountRef).getDouble(FIELD_UNREAD_MESSAGE_COUNT)
                    ?: 0.0

//            var unreadCount = 0
//            ids.forEach { id ->
//                val message = transaction.get(messageCollectionRef.document(id))
//                if (message.getBoolean(FIELD_IS_READ) != true) unreadCount++
//            }

            ids.forEach { id -> transaction.update(messageCollectionRef.document(id), FIELD_IS_READ, true) }

            val resultCount = userUnreadMessageCount - ids.size
            transaction.update(unreadMessageCountRef, FIELD_UNREAD_MESSAGE_COUNT, if (resultCount < 0) 0 else resultCount)

            val resultUnreadChatCount = userUnreadChatMessageCount - ids.size
            transaction.update(unreadChatMessageCountRef, FIELD_UNREAD_MESSAGE_COUNT, if (resultUnreadChatCount < 0) 0 else resultUnreadChatCount)

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

    override fun subscribeChatUnreadMessageCount(chatId: String): Flowable<Int> {
        val unreadMessageCountRef = firestore.collection(COLLECTION_CHATS).document(chatId).collection(COLLECTION_USERS).document(appData.getUser().user_id.toString())
        return RxFirestore.observeDocumentRef(unreadMessageCountRef)
                .map { it.getDouble(FIELD_UNREAD_MESSAGE_COUNT)?.toInt() ?: 0 }
    }

    override fun subscribeChatLastMessage(): Flowable<LocalNotification> {
        val lastMsgRef = firestore.collection(COLLECTION_USERS).document(appData.getUser().user_id.toString())
        return RxFirestore.observeDocumentRef(lastMsgRef)
                .map { it.toObject(LocalNotification::class.java) }

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

    override fun getChat(chatId: String): Single<UserChat> {
        return call(api.getChat(chatId))
    }
}