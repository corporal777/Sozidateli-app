package com.example.repository

import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.google.firebase.firestore.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

interface ChatRepository {

    fun getChatMessageQuery(chatId: String): Query

    fun sendChatMessage(chatId: String, message: ChatMessage): Completable

    fun singInFirebase(): Completable
}