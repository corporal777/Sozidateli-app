package com.example.repository

import com.example.data.AppData
import com.example.data.models.ChatMessage
import com.example.util.COLLECTION_CHATS
import com.example.util.COLLECTION_MESSAGES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirebaseAuth
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import javax.inject.Inject


class ChatRepositoryImpl
@Inject constructor(
        private val firestore: FirebaseFirestore,
        private val firebaseAuth: FirebaseAuth,
        private val appData: AppData
) : ApiRepository(appData), ChatRepository {

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

    override fun singInFirebase(): Completable {
        return RxFirebaseAuth.signInAnonymously(firebaseAuth).flatMapCompletable { Completable.complete() }
    }
}