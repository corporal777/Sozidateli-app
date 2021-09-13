package com.example.data.socket

import android.util.Log
import com.example.data.AppData
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import ru.houseofapps.chat.models.ChatConnectionStatus
import java.net.URI
import java.net.URISyntaxException
import javax.inject.Inject

class SocketIOManagerImpl
@Inject constructor(val appData: AppData): SocketIOManager {

    private val ERROR_TOKEN_MISS = "No authorization token was found"
    private val ERROR_TOKEN_INVALID = "Invalid token"
    private var mSocket: Socket? = null

    private var connectionStatusSubject = PublishSubject.create<ChatConnectionStatus>()
    private var connectionStatus = ChatConnectionStatus.DISCONNECTED
        set(value) {
            field = value
            connectionStatusSubject.onNext(value)
        }

    init {
        connect()
    }

    private fun connect() {
        try {
            mSocket = IO.socket(URI.create("https://alfa-socket-data-provider.sozidateli.ru/"), IO.Options().apply {
                query = "token=Token "+appData.token
                //transports = arrayOf(Polling.NAME/*, WebSocket.NAME*/)
            })
            Log.i("ChatSocket", "Connected to socket")
        } catch (e: URISyntaxException) {
            Log.i("ChatSocket", "Not connected to socket")
        }
    }

    override fun connectToSocket(): Completable =
        Completable.fromAction {
            mSocket = IO.socket(URI.create("https://alfa-socket-data-provider.sozidateli.ru/"), IO.Options().apply {
                query = "token=Token "+appData.token
                //transports = arrayOf(Polling.NAME/*, WebSocket.NAME*/)
            }).apply {
                on(Socket.EVENT_CONNECT_ERROR) {
                    //connectionStatus = ChatConnectionStatus.ERROR
                    Log.i("ChatSocket", "Error event: " + it.contentToString())
                }
                on(Socket.EVENT_CONNECT) {
                    //connectionStatus = ChatConnectionStatus.CONNECTED
                    Log.i("ChatSocket", "Connect event: " + it.contentToString())
                }
                on(Socket.EVENT_DISCONNECT) {
                    //connectionStatus = ChatConnectionStatus.DISCONNECTED
                    Log.i("ChatSocket", "Disconnect event: " + it.contentToString())
                }
                on(Manager.EVENT_CLOSE) {
                    Log.i("ChatSocket", "Close event: " + it.contentToString())
                }
                on(Manager.EVENT_ERROR) {
                    val error = it[0]?.toString()
                    if (error == ERROR_TOKEN_MISS || error == ERROR_TOKEN_INVALID) {
                        disconnect()
                        //connectionStatus = ChatConnectionStatus.ERROR
                        Log.i("ChatSocket", "Close event: " + it.contentToString())
                    }
                }
                Log.i("ChatSocket", "Connected")
                connect()
            }
        }

    override fun subscribeToChatUpdate(chatId: String): Flowable<List<String>> =
        Flowable.fromPublisher {
            mSocket?.emit("joinRoom", chatId)
            Log.i("ChatSocket", "Started listening: $chatId")
            it.onNext(emptyList())
            mSocket?.on("new-message") { data ->
                Log.i("ChatSocket", "Data: " + data.toString())
                //it.onNext(data)
            }
            //mSocket?.connect()
        }

    override fun stopListenChatUpdate(chatId: String) {
        mSocket?.emit("leaveRoom", chatId)
        Log.i("ChatSocket", "Stopped listening")
        mSocket?.off("leaveRoom")
    }

    override fun disconnectFromSocket() {
        mSocket?.disconnect()
        Log.i("ChatSocket", "Disconnected")
    }
}