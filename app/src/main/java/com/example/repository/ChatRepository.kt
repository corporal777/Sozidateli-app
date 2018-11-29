package com.example.repository

import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

interface ChatRepository {

    fun subscribeChatMessages(chatId: String): Flowable<List<ChatMessage>>

    fun subscribeOnChatList(): Flowable<List<UserChat>>

    fun createChatIfNeed(userId:String): Observable<UserChat>
}