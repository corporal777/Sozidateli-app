package com.example.data.socket

import com.example.data.models.ApiNewResponse
import com.example.data.models.MessageModel
import io.reactivex.Completable
import io.reactivex.Flowable

interface SocketIOManager {

    fun connectToSocket(): Completable
    fun subscribeToChatUpdate(chatId: String): Flowable<ApiNewResponse<List<MessageModel>>>
    fun stopListenChatUpdate(chatId: String)
    fun disconnectFromSocket()
}