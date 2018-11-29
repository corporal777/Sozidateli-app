package com.example.repository

import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.example.util.COLLECTION_CHATS
import com.example.util.COLLECTION_MESSAGES
import com.example.util.COLLECTION_USERS
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
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

    override fun subscribeOnChatList(): Flowable<List<UserChat>> {
        val query = firestore.collection(COLLECTION_USERS)
                .document("1") //TODO own user id
                .collection(COLLECTION_CHATS)
        Timber.tag("CHAT_T").d("QUERY ${query.path}")

        return RxFirestore.observeQueryRef(query, UserChat::class.java)
    }

    override fun createChatIfNeed(userId: String): Observable<UserChat> {
        val queryChats = firestore.collection(COLLECTION_CHATS).document().collection(COLLECTION_MESSAGES)


        val userChat = UserChat(userId, queryChats.parent?.id)

        val queryUsers = firestore.collection(COLLECTION_USERS)
                .document("1") //TODO own user id
                .collection(COLLECTION_CHATS)
                .document(userId)

        return Observable.create<UserChat> { t ->
            RxFirestore.getDocument(queryUsers, UserChat::class.java)
                    .doOnComplete {
                        t.onNext(userChat)
                    }
                    .subscribe {
                        t.onNext(it)
                    }
        }.flatMap {
            RxFirestore.setDocument(queryUsers, it).toObservable<UserChat>()
        }
    }

}