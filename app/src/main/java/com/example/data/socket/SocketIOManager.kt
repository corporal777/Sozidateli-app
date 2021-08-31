package com.example.data.socket

import io.reactivex.Completable
import io.reactivex.Flowable

interface SocketIOManager {

    fun connectToSocket(): Completable
    fun subscribeToChatUpdate(chatId: String): Flowable<List<String>>
    fun stopListenChatUpdate(chatId: String)
    fun disconnectFromSocket()
}