package com.example.repository

import com.example.data.AppData
import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.example.data.models.UserChat.Companion.FIELD_LAST_MESSAGE
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
        private val firestore: FirebaseFirestore,
        private val appData: AppData
) : ChatRepository {

    override fun subscribeOnChatList(): Flowable<List<UserChat>> {
        val query = firestore.collection(COLLECTION_USERS)
                .document(appData.uid)
                .collection(COLLECTION_CHATS)
        Timber.tag("CHAT_T").d("QUERY ${query.path}")

        return RxFirestore.observeQueryRef(query, UserChat::class.java)
    }

    override fun createChatIfNeed(userId: String): Observable<UserChat> {
        val queryChats = firestore.collection(COLLECTION_CHATS).document().collection(COLLECTION_MESSAGES)


        val userChatOpponent = UserChat(appData.uid, queryChats.parent?.id)

        val queryOwn = firestore.collection(COLLECTION_USERS)
                .document(appData.uid)
                .collection(COLLECTION_CHATS)
                .document(userId)

        val queryChatOpponent = firestore.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_CHATS)
                .document(appData.uid)

        return Observable.create<UserChat> { t ->
            RxFirestore.getDocument(queryChatOpponent, UserChat::class.java)
                    .doOnComplete {
                        t.onNext(userChatOpponent)
                    }
                    .subscribe {
                        t.onNext(it)
                    }
        }.map {
            Timber.tag("CREATE_CHAT").d("1 ${it.userId}")
            RxFirestore.setDocument(queryChatOpponent, it)
                    .subscribe()
            return@map it
        }.map {
            Timber.tag("CREATE_CHAT").d("2 ${it.userId}")
            var ownUserChat = UserChat(appData.uid,it.chatId)
            RxFirestore.setDocument(queryOwn, ownUserChat)
                    .subscribe()
            return@map ownUserChat
        }
    }

    override fun getChatMessageQuery(chatId: String) = firestore.collection(COLLECTION_CHATS)
            .document(chatId)
            .collection(COLLECTION_MESSAGES)
            .orderBy(ChatMessage.FIELD_SEND_AT)

    override fun getChatListQuery() = firestore.collection(COLLECTION_USERS)
            .document(appData.uid)
            .collection(COLLECTION_CHATS)
    //.whereGreaterThan("lastMessage","")

    override fun sendChatMessage(chatId: String, toUser: String, message: ChatMessage): Completable {
        val messageMap = message.toMap()

        return RxFirestore.runTransaction(firestore) {
            it.set(firestore.collection(COLLECTION_CHATS)
                    .document(chatId)
                    .collection(COLLECTION_MESSAGES)
                    .document(), messageMap)

            it.set(firestore.collection(COLLECTION_USERS)
                    .document(message.senderId)
                    .collection(COLLECTION_CHATS)
                    .document(toUser), mapOf(FIELD_LAST_MESSAGE to messageMap))

            it.set(firestore.collection(COLLECTION_USERS)
                    .document(toUser)
                    .collection(COLLECTION_CHATS)
                    .document(message.senderId), mapOf(FIELD_LAST_MESSAGE to messageMap))
        }
    }
}