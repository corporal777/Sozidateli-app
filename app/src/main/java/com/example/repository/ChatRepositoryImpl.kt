package com.example.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Flowable
import timber.log.Timber
import javax.inject.Inject


class ChatRepositoryImpl
@Inject constructor(
        private val firestore: FirebaseFirestore
) : ChatRepository {

    override fun subscribeChatMessages(chatId: String): Flowable<QuerySnapshot> {
        val query = firestore.collection("userChats")
                .document("1")
                .collection("chats")
        Timber.tag("CHAT_T").d("QUERY ${query.path}")
        return RxFirestore.observeQueryRef(query)
    }
}