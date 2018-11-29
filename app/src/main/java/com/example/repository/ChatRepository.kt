package com.example.repository

import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Flowable

interface ChatRepository {

    fun subscribeChatMessages(chatId: String): Flowable<QuerySnapshot>
}