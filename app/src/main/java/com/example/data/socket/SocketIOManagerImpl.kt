package com.example.data.socket

import android.util.Log
import com.example.data.AppData
import com.example.data.models.ApiNewResponse
import com.example.data.models.MessageAcknowledgeModel
import com.example.data.models.MessageModel
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Flowable
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.engineio.client.transports.Polling
import io.socket.parseqs.ParseQS
import okhttp3.OkHttpClient
import java.net.URI
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class SocketIOManagerImpl
@Inject constructor(val appData: AppData) : SocketIOManager {

    private var mSocket: Socket? = null
    private var okHttpClient: OkHttpClient

    init {
        okHttpClient = getHttpClient()
        connect()
    }

    private fun connect() {
        try {
            mSocket = IO.socket(URI.create("https://alfa-socket-data-provider.sozidateli.ru/"), IO.Options().apply {
                query = ParseQS.encode(
                        hashMapOf("token" to "Token ${appData.token}"))
                transports = arrayOf(Polling.NAME)
                callFactory = okHttpClient
                webSocketFactory = okHttpClient
            })
            Log.i("ChatSocket", "Connected to socket")
        } catch (e: URISyntaxException) {
            Log.i("ChatSocket", "Not connected to socket")
        }
    }

    override fun connectToSocket(): Completable =
            Completable.fromAction {
                /*mSocket = IO.socket(URI.create("https://alfa-socket-data-provider.sozidateli.ru/"), IO.Options().apply {
                    query = "token=Token " + appData.token
                    transports = arrayOf(Polling.NAME/*, WebSocket.NAME*/)
                }).apply {
                    connect()
                    on(Manager.EVENT_TRANSPORT) {
                        Log.i("ChatSocket", "Transport event: " + it.contentToString())
                    }
                    on(Socket.EVENT_CONNECT_ERROR) {
                        Log.i("ChatSocket", "Error event: " + it.contentToString())
                    }
                    on(Socket.EVENT_CONNECT) {
                        Log.i("ChatSocket", "Connect event: " + it.contentToString())
                        mSocket?.emit("joinRoom", 1)
                        Log.i("ChatSocket", "Started listening: 1")
                        mSocket?.on("new-message") { data ->
                            /*val id = (data[0] as JSONObject).getInt("id")
                            val chat = (data[0] as JSONObject).getInt("chat")
                            val createdDate = (data[0] as JSONObject).getString("createdDate")
                            val createdBy = (data[0] as JSONObject).getInt("createdBy")
                            val message = (data[0] as JSONObject).getString("message")*/
                            Log.i("ChatSocket", "Data: " + data.toString())
                            //it.onNext(data)
                        }
                    }
                    on(Socket.EVENT_DISCONNECT) {
                        Log.i("ChatSocket", "Disconnect event: " + it.contentToString())
                    }
                    on(Manager.EVENT_CLOSE) {
                        Log.i("ChatSocket", "Close event: " + it.contentToString())
                    }
                    on(Manager.EVENT_ERROR) {
                        Log.i("ChatSocket", "Close event: " + it.contentToString())
                    }
                    Log.i("ChatSocket", "Connected")
                }*/
            }

    override fun subscribeToChatUpdate(chatId: String): Flowable<ApiNewResponse<List<MessageModel>>> =
            Flowable.fromPublisher { res ->
                mSocket?.connect()
                mSocket?.on(Manager.EVENT_TRANSPORT) {
                    Log.i("ChatSocket", "Transport event: " + it.contentToString())
                }
                mSocket?.on(Socket.EVENT_CONNECT_ERROR) {
                    Log.i("ChatSocket", "Error event: " + it.contentToString())
                }
                mSocket?.on(Socket.EVENT_CONNECT) {
                    Log.i("ChatSocket", "Connect event: " + it.contentToString())
                    mSocket?.emit("joinRoom", chatId)
                    Log.i("ChatSocket", "Started listening: $chatId")
                    mSocket?.on("new-message") { data ->
                        Log.i("ChatSocket", "Data: " + data.toString())
                        val lastMessage = Gson().fromJson(data[0].toString(), MessageModel::class.java)
                        val result = ApiNewResponse(listOf(lastMessage), 1)
                        res.onNext(result)
                    }
                }
                mSocket?.on(Socket.EVENT_DISCONNECT) {
                    Log.i("ChatSocket", "Disconnect event: " + it.contentToString())
                    mSocket?.off("new-message")
                }
                mSocket?.on(Manager.EVENT_CLOSE) {
                    Log.i("ChatSocket", "Close event: " + it.contentToString())
                }
                mSocket?.on(Manager.EVENT_ERROR) {
                    Log.i("ChatSocket", "Close event: " + it.contentToString())
                }
                Log.i("ChatSocket", "Connected")
                /*mSocket?.emit("joinRoom", chatId)
                Log.i("ChatSocket", "Started listening: $chatId")
                //res.onNext(emptyList())
                mSocket?.on("new-message") { data ->
                    Log.i("ChatSocket", "Data: " + data.toString())
                    //res.onNext(data)
                }*/
            }

    override fun stopListenChatUpdate(chatId: String) {
        mSocket?.emit("leaveRoom", chatId)
        Log.i("ChatSocket", "Stopped listening")
        mSocket?.disconnect()
        mSocket?.off("leaveRoom")
        mSocket?.off("new-message")
    }

    override fun disconnectFromSocket() {
        mSocket?.disconnect()
        Log.i("ChatSocket", "Disconnected")
    }

    private fun getHttpClient(): OkHttpClient {
        val myHostnameVerifier = HostnameVerifier { _, _ ->
            return@HostnameVerifier true
        }
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) { }

            override fun checkServerTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) { }

            override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate>? {
                return arrayOf()
            }
        })
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, null)
        return OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .hostnameVerifier(myHostnameVerifier)
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
    }
}