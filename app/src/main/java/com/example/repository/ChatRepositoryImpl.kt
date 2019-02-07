package com.example.repository

import com.example.data.AppData
import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.example.util.COLLECTION_CHATS
import com.example.util.COLLECTION_MESSAGES
import com.example.util.COLLECTION_USERS
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import timber.log.Timber
import javax.inject.Inject


class ChatRepositoryImpl
@Inject constructor(
        private val firestore: FirebaseFirestore,
        private val appData: AppData
) : ApiRepository(appData), ChatRepository {

    override fun subscribeOnChatList(): Flowable<List<UserChat>> {
        val query = firestore.collection(COLLECTION_USERS)
                .document(appData.getUser().user_id.toString())
                .collection(COLLECTION_CHATS)
        Timber.tag("CHAT_T").d("QUERY ${query.path}")

        return RxFirestore.observeQueryRef(query, UserChat::class.java)
    }

    override fun getChatMessageQuery(chatId: String) = firestore.collection(COLLECTION_CHATS)
            .document(chatId)
            .collection(COLLECTION_MESSAGES)
            .orderBy(ChatMessage.FIELD_SEND_AT)


    override fun sendChatMessage(chatId: String, message: ChatMessage): Completable {
        val messageMap = message.toMap()
        return RxFirestore.setDocument(firestore.collection(COLLECTION_CHATS)
                .document(chatId)
                .collection(COLLECTION_MESSAGES)
                .document(), messageMap)
    }
}