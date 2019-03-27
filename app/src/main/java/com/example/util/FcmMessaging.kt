package com.example.util

import android.annotation.SuppressLint
import com.example.App
import com.example.data.models.UserChat
import com.example.data.prefs.AppPrefs
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import performOnBackgroundOutOnMain

class FcmMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        remoteMessage?.let { sendNotification(it) }
    }

    override fun onNewToken(p0: String?) {

    }

    @SuppressLint("CheckResult")
    private fun sendNotification(remoteMessage: RemoteMessage) {
        if ((application as? App)?.appIsRunning == true) return

        var userChat: UserChat? = null

        userChat = Gson().fromJson<UserChat>(remoteMessage.data.values.elementAt(0), UserChat::class.java)

        //userChat?.messageId = remoteMessage.messageId

        val firestore = FirebaseFirestore.getInstance()

        val msgRef = firestore.collection(COLLECTION_CHATS).document(userChat.id.toString()).collection(COLLECTION_MESSAGES).document(userChat.messageId!!)

        RxFirestore.getDocument(msgRef)
                .flatMapCompletable {
                    val isShowed = it.getBoolean(FIELD_IS_SHOWED) ?: false
                    if (!isShowed) {
                        NotificationUtill.showPushChatNotification(this, userChat)
                        RxFirestore.runTransaction(firestore) {
                            val refLastMsg = firestore.collection(COLLECTION_USERS).document(AppPrefs(this).userId.toString())
                            val lastMsgId = it.get(refLastMsg).get(FIELD_MESSAGE_ID)

                            it.update(msgRef, mapOf(
                                    FIELD_IS_SHOWED to true
                            ))


                            if (lastMsgId == userChat?.messageId) {
                                it.update(refLastMsg, mapOf(
                                        FIELD_IS_SHOWED to true
                                ))
                            }

                            null
                        }
                    } else {
                        Completable.complete()
                    }
                }
                .performOnBackgroundOutOnMain()
                .subscribe({

                }, {
                    it.printStackTrace()
                })


    }

    companion object {
        private const val DATA_MESSAGE_ID = "message_id"
    }
}