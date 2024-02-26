package com.example.data.socket

import com.example.data.models.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface SocketIOManager {
    fun subscribeNewChatMessage(): Flowable<ApiNewResponse<List<MessageModel>>>
    fun subscribeToChatUpdate(): Flowable<ApiNewResponse<List<MessageModel>>>
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

    fun subscribeTotalNotificationsTypesCount(): Flowable<NotificationsTypesModel>
    fun subscribeNotificationsInvitesCount(): Flowable<NotificationInviteModel>

    fun connectToAuthWithQrCode(code : String, socketId : String?): Completable
    fun confirmAuthWithQrCode(code : String, socketId : String?, isAccept : Boolean): Completable
    fun subscribeAuthQrCode(): Flowable<QrAuthResponse>
}