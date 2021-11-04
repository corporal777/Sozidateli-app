package com.example.data.socket

import com.example.data.models.ApiNewResponse
import com.example.data.models.MessageModel
import com.example.data.models.RoomUnreadMessageCount
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface SocketIOManager {
    fun subscribeToChatUpdate(): Flowable<ApiNewResponse<List<MessageModel>>>
    fun stopListenChatUpdate()
    fun connectToChat(chatId: String): Completable
    fun disconnectFromChat(chatId: String): Completable
    fun disconnectFromSocket()
    fun connect(): Flowable<SocketConnectionState>
    fun isConnected(): Single<Boolean>
    fun subscribeToTotalMessagesCount(): Flowable<Int>
    fun subscribeToMessagesCount(): Flowable<RoomUnreadMessageCount>
    fun subscribeToBannedList(chatId: String): Flowable<String>
    fun subscribeToInviteChange(chatId: String): Flowable<String>
    fun subscribeToInvitesCount(): Flowable<Int>
    fun connectToUpdates(): Completable
    fun subscribeToTotalNotificationsCount(): Flowable<Int>
}