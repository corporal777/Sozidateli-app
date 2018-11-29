package com.example.repository

import com.example.data.models.ChatMessage
import com.example.util.COLLECTION_CHATS
import com.example.util.COLLECTION_MESSAGES
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Flowable
import timber.log.Timber
import javax.inject.Inject


class ChatRepositoryImpl
@Inject constructor(
        private val firestore: FirebaseFirestore
) : ChatRepository {

    override fun subscribeChatMessages(chatId: String): Flowable<List<ChatMessage>> {
        val query = firestore.collection(COLLECTION_CHATS)
                .document(chatId)
                .collection(COLLECTION_MESSAGES)
        Timber.tag("CHAT_T").d("QUERY ${query.path}")
        return RxFirestore.observeQueryRef(query, ChatMessage::class.java)
    }
}