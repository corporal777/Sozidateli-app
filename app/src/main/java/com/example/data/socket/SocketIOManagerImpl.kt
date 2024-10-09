package com.example.data.socket

import android.util.Log
import com.example.app.BuildConfig
import com.example.data.AppData
import com.example.data.models.*
import com.google.gson.Gson
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
import io.socket.parseqs.ParseQS
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import timber.log.Timber
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
    private var connectionStatusSubject = PublishSubject.create<SocketConnectionState>()
    private var connectionStatus = SocketConnectionState.DISCONNECT
        set(value) {
            field = value
            connectionStatusSubject.onNext(value)
        }

    init {
        okHttpClient = getHttpClient()
    }

    override fun connect(): Flowable<SocketConnectionState> {
        try {
            mSocket = IO.socket(URI.create(BuildConfig.SOCKET_URL), IO.Options().apply {
                query = ParseQS.encode(hashMapOf("token" to "Token ${appData.token}"))
                transports = arrayOf(WebSocket.NAME, Polling.NAME)
                callFactory = okHttpClient
                webSocketFactory = okHttpClient
            }).apply {
                on(Manager.EVENT_OPEN) {
                    logErrorSocket("Open event: " + it.contentToString())
                }
                on(Socket.EVENT_CONNECT_ERROR) {
                    logErrorSocket("Error event: " + it.contentToString())
                    connectionStatus = SocketConnectionState.ERROR
                }
                on(Socket.EVENT_CONNECT) {
                    logErrorSocket("Connect event: " + it.contentToString())
                    connectionStatus = SocketConnectionState.CONNECTED
                }
                on(Socket.EVENT_DISCONNECT) {
                    logErrorSocket("Disconnect event: " + it.contentToString())
                    connectionStatus = SocketConnectionState.ERROR
                }
                on(Manager.EVENT_CLOSE) {
                    logErrorSocket("Close event: " + it.contentToString())
                    connectionStatus = SocketConnectionState.ERROR
                }
                on(Manager.EVENT_ERROR) {
                    logErrorSocket("Close event: " + it.contentToString())
                    connectionStatus = SocketConnectionState.ERROR
                    disconnect()
                }
                connect()
                logErrorSocket("Connected")
            }
            logErrorSocket("Token ${appData.token}")
            logErrorSocket("Connected to socket")
        } catch (e: URISyntaxException) {
            logErrorSocket("Not connected to socket")
        }
        return connectionStatusSubject.toFlowable(BackpressureStrategy.BUFFER)
    }

    override fun connectToChat(chatId: String): Completable =
        Completable.fromAction {
            mSocket?.emit("joinRoom", chatId)
            Log.i("ChatSocket", "Started listening: $chatId")
        }

    override fun connectToUpdates(): Completable =
        Completable.fromAction {
            mSocket?.emit("refresh")
            logErrorSocket("Started refresh")
        }

    override fun disconnectFromChat(chatId: String): Completable =
        Completable.fromAction {
            mSocket?.emit("leaveRoom", chatId)
            Log.i("ChatSocket", "Stopped listening: $chatId")
        }

    override fun subscribeToChatUpdate(): Flowable<ApiNewResponse<List<MessageModel>>> =
        Flowable.create({ emitter ->
            val listener = Emitter.Listener { args ->
                Log.i("ChatSocket", "Data: " + args.toString())
                val lastMessage = Gson().fromJson(args[0].toString(), MessageModel::class.java)
                val result = ApiNewResponse(listOf(lastMessage), 1)
                emitter.onNext(result)
            }
            mSocket?.on("new-message", listener)
            Log.i("ChatSocket", "Started listening new-message event")

            emitter.setCancellable {
                Log.i("ChatSocket", "Stopped listening new-message")
                mSocket?.off("new-message", listener)
            }
        }, BackpressureStrategy.LATEST)

    override fun subscribeNewChatMessage(): Flowable<ApiNewResponse<List<MessageModel>>> =
        Flowable.create({ emitter ->
            val listener = Emitter.Listener { args ->
                Log.i("ChatSocket", "Data: " + args.toString())
                val lastMessage = Gson().fromJson(args[0].toString(), MessageModel::class.java)
                val result = ApiNewResponse(listOf(lastMessage), 1)
                emitter.onNext(result)
            }

            mSocket?.on("user-new-message", listener)
            Log.i("ChatSocket", "Started listening new-message event")

            emitter.setCancellable {
                Log.i("ChatSocket", "Stopped listening new-message")
                mSocket?.off("user-new-message", listener)
            }
        }, BackpressureStrategy.LATEST)

    override fun subscribeToTotalNotificationsCount(): Flowable<Int> =
        Flowable.create({ emitter ->
            val listener = Emitter.Listener { args ->
                if (args == null || args[0] == null) return@Listener
                emitter.onNext(args[0].toString().toInt())
            }
            mSocket?.on("notification-count", listener)
            logErrorSocket("Started listening notification-count event")
            emitter.setCancellable { mSocket?.off("notification-count", listener) }
        }, BackpressureStrategy.LATEST)

    override fun subscribeNotificationsInvitesCount(): Flowable<NotificationInviteModel> {
        return Flowable.create({ emitter ->
            val listener = Emitter.Listener { args ->
                if (args == null || args[0] == null) return@Listener
                val data = Gson().fromJson(args[0].toString(), NotificationInviteModel::class.java)
                emitter.onNext(data)
            }
            mSocket?.on("notification-invite-types-count", listener)
            logErrorSocket("Started listening notification-invite-types-count event")
            emitter.setCancellable { mSocket?.off("notification-invite-types-count", listener) }
        }, BackpressureStrategy.LATEST)
    }

    override fun subscribeTotalNotificationsTypesCount(): Flowable<NotificationsTypesModel> {
        return Flowable.create({ emitter ->
            val listener = Emitter.Listener { args ->
                if (args == null || args[0] == null) return@Listener
                val data = Gson().fromJson(args[0].toString(), NotificationsTypesModel::class.java)
                emitter.onNext(data)
            }
            mSocket?.on("notification-types-count", listener)
            logErrorSocket("Started listening notification-types-count event")
            emitter.setCancellable { mSocket?.off("notification-types-count", listener) }
        }, BackpressureStrategy.LATEST)
    }

    override fun subscribeToInvitesCount(): Flowable<Int> =
        Flowable.create({ emitter ->
            val listener = Emitter.Listener { args ->
                Log.i("ChatSocket", "Data: " + args.toString())
                //emitter.onNext(args[0].toString().toInt())
                if (args.first() != null) emitter.onNext(args.firstOrNull().toString().toInt())
            }

            mSocket?.on("user-count-of-invites", listener)
            Log.i("ChatSocket", "Started listening user-count-of-invites event")

            emitter.setCancellable {
                Log.i("ChatSocket", "Stopped listening user-count-of-invites")
                mSocket?.off("user-count-of-invites", listener)
            }
        }, BackpressureStrategy.LATEST)

    override fun subscribeToTotalMessagesCount(): Flowable<Int> =
        Flowable.create({ emitter ->
            val listener = Emitter.Listener { args ->
                Log.i("ChatSocket", "Data: " + args.toString())
                //emitter.onNext(args[0].toString().toInt())
                if (args.first() != null) emitter.onNext(args.firstOrNull().toString().toInt())
            }

            mSocket?.on("unread-total-message-count", listener)
            Log.i("ChatSocket", "Started listening unread-total-message-count event")

            emitter.setCancellable {
                Log.i("ChatSocket", "Stopped listening unread-total-message-count")
                mSocket?.off("unread-total-message-count", listener)
            }
        }, BackpressureStrategy.LATEST)

    override fun subscribeToMessagesCount(): Flowable<RoomUnreadMessageCount> =
        Flowable.create({ emitter ->
            val listener = Emitter.Listener { args ->
                Log.i("ChatSocket", "Data: " + args.toString())
                emitter.onNext(
                    Gson().fromJson(
                        args[0].toString(),
                        RoomUnreadMessageCount::class.java
                    )
                )
            }

            mSocket?.on("unread-room-message-count", listener)
            Log.i("ChatSocket", "Started listening unread-room-message-count event")

            emitter.setCancellable {
                Log.i("ChatSocket", "Stopped listening unread-room-message-count")
                mSocket?.off("unread-room-message-count", listener)
            }
        }, BackpressureStrategy.LATEST)

    override fun subscribeToInviteChange(chatId: String): Flowable<String> =
        Flowable.create({ emitter ->
            val listener = Emitter.Listener { args ->
                Log.i("ChatSocket", "Data: " + args.toString())
                val invite = Gson().fromJson(args[0].toString(), ChatModel::class.java)
                emitter.onNext(invite.id.toString())
            }

            mSocket?.on("invite-users", listener)
            Log.i("ChatSocket", "Started listening invite-users event")

            emitter.setCancellable {
                Log.i("ChatSocket", "Stopped listening invite-users")
                mSocket?.off("invite-users", listener)
            }
        }, BackpressureStrategy.LATEST)

    override fun subscribeToBannedList(chatId: String): Flowable<String> =
        Flowable.create({ emitter ->
            val listener = Emitter.Listener { args ->
                Log.i("ChatSocket", "Data: " + args.toString())
                if (args[0].toString() != "[]") {
                    val chat = Gson().fromJson(args[0].toString(), ChatModel::class.java)
                    emitter.onNext(chat.id.toString())
                }
            }

            mSocket?.on("users-banned-list", listener)
            Log.i("ChatSocket", "Started listening users-banned-list event")

            emitter.setCancellable {
                Log.i("ChatSocket", "Stopped listening users-banned-list")
                mSocket?.off("users-banned-list", listener)
            }
        }, BackpressureStrategy.LATEST)


    override fun connectToAuthWithQrCode(code: String, socketId: String?): Completable =
        Completable.fromAction {
            val obj = JSONObject().apply {
                put("socket", socketId)
                put("qr", code)
            }
            mSocket?.emit("qrRead", obj)
            logErrorSocket("QrAuthSocket", "Send message to connect")
        }

    override fun confirmAuthWithQrCode(code: String, socketId: String?, isAccept: Boolean): Completable =
        Completable.fromAction {
            val obj = JSONObject().apply {
                put("socket", socketId)
                put("qr", code)
                put("isAccept", isAccept)
            }
            mSocket?.emit("qrAccept", obj)
            logErrorSocket("QrAuthSocket", "Send message to confirm")
        }


    override fun subscribeAuthQrCode(): Flowable<QrAuthResponse> =
        Flowable.create({ emitter ->
            val listener = Emitter.Listener { args ->
                if (args[0] == null || args[0].toString() == "[]") return@Listener
                val data = Gson().fromJson(args[0].toString(), QrAuthResponse::class.java)
                emitter.onNext(data)
            }
            mSocket?.once("check", listener)
            logErrorSocket("QrAuthSocket", "Started listening auth with qr code")
        }, BackpressureStrategy.LATEST)


    override fun disconnectFromSocket() {
        mSocket?.disconnect()
        logErrorSocket("Disconnected")
    }

    override fun isConnected(): Single<Boolean> =
        Single.just(connectionStatus == SocketConnectionState.CONNECTED)

    private fun getHttpClient(): OkHttpClient {
        val myHostnameVerifier = HostnameVerifier { _, _ ->
            return@HostnameVerifier true
        }
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) {}

            override fun checkServerTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) {}

            override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate>? {
                return arrayOf()
            }
        })
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, null)
        val logInterceptor = HttpLoggingInterceptor { message ->
            Timber.tag("Socket_DATA").d(message)
        }
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .hostnameVerifier(myHostnameVerifier)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .addInterceptor(logInterceptor)
            .build()
    }

    private fun logErrorSocket(message: String) = Log.e("Socket", message)
    private fun logErrorSocket(tag: String, message: String) = Log.e(tag, message)
    private fun logInfoSocket(message: String) = Log.e("Socket", message)
}