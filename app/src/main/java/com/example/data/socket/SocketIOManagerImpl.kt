package com.example.data.socket

import android.util.Log
import com.example.data.AppData
import io.reactivex.Completable
import io.reactivex.Flowable
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import javax.inject.Inject

class SocketIOManagerImpl
@Inject constructor(val appData: AppData): SocketIOManager {

    private var mSocket: Socket? = null

    init {
        connect()
    }

    private fun connect() {
        try {
            val options = IO.Options()
            options.query = "token=Token "+appData.token
            mSocket = IO.socket("https://alfa-socket-data-provider.sozidateli.ru/", options)
            Log.i("ChatSocket", "Connected to socket")
        } catch (e: URISyntaxException) {
            Log.i("ChatSocket", "Not connected to socket")
        }
    }

    override fun connectToSocket(): Completable =
        Completable.fromAction {
            mSocket?.connect()
            Log.i("ChatSocket", "Connected")
        }

    override fun subscribeToChatUpdate(chatId: String): Flowable<List<String>> =
        Flowable.fromPublisher {
            mSocket?.emit("joinRoom", chatId)
            Log.i("ChatSocket", "Started listening")
            mSocket?.on("new-message") { data ->
                Log.i("ChatSocket", "Data: " + data.toString())
                //it.onNext(data)
            }
        }

    override fun stopListenChatUpdate(chatId: String) {
        mSocket?.emit("leaveRoom", chatId)
        Log.i("ChatSocket", "Stopped listening")
        //mSocket?.off("leaveRoom")
    }

    override fun disconnectFromSocket() {
        mSocket?.disconnect()
        Log.i("ChatSocket", "Disconnected")
    }
}