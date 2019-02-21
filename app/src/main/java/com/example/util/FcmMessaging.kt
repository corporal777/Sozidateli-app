package com.example.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import bundleOf
import com.example.App
import com.example.R
import com.example.data.AppData
import com.example.data.models.UserChat
import com.example.data.models.user.User
import com.example.ui.chat.ChatFragment
import com.example.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Observable
import performOnMain
import java.lang.Exception



class FcmMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        remoteMessage?.let { sendNotification(it) }
    }

    override fun onNewToken(p0: String?) {

    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        var userChat: UserChat? = null

        var title = remoteMessage.notification?.title
        var body = remoteMessage.notification?.body

        //if (remoteMessage.data.containsKey("new-chat-msg")) {
        userChat = Gson().fromJson<UserChat>(remoteMessage.data.values.elementAt(0), UserChat::class.java)
        //}

        if(userChat?.user==null){
            userChat?.user = User(user_id = 1606, user_avatar = "http://ha-slsp.ru/pics/users/1564.jpg", user_name = "Koala", user_last_name = "Big")
        }

        if(this.application is App){
            val chatId = (application as App).currentChatID
            chatId?.let {
                if(it == userChat?.id) return
            }
        }



        Observable.fromCallable {

            Picasso.get().load(userChat?.user?.user_avatar.let { if (it.isNullOrEmpty()) null else it })
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(CropCircleTransformation())
                    .into(object : Target {
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            createChatNotifiaction(userChat, null, remoteMessage)
                        }

                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            createChatNotifiaction(userChat, bitmap, remoteMessage)
                        }
                    })

        }.performOnMain().subscribe()


    }

    private fun createChatNotifiaction(userChat: UserChat?, bitmap: Bitmap?, remoteMessage: RemoteMessage) {
        val channelId = getString(R.string.app_name)

        val summaryNotification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(bitmap)
                .setGroup(userChat?.id)
                .setGroupSummary(true)
                .build()

        NotificationCompat.Builder(this, channelId)
                .setDefaults(DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(userChat?.user?.fullName)
                .setContentText(userChat?.lastMessage)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(createNotificationIntent(remoteMessage))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(bitmap)
                .setGroup(userChat?.id)
                .apply {
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(userChat?.id!!.toInt(),summaryNotification)
                    notificationManager.notify(userChat.lastMessageDate?.hashCode()
                            ?: 0, this.build())
                }
    }

    private fun createNotificationIntent(remoteMessage: RemoteMessage): PendingIntent {
        /* var chatId = remoteMessage.data["id"]
         var senderId = remoteMessage.data["last_message_user_id"]*/

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtras(bundleOf(
                    "chatId" to remoteMessage.data["id"],
                    "last_message_user_id" to remoteMessage.data["last_message_user_id"]
            ))
        }

        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        private const val DATA_MESSAGE_ID = "message_id"
    }
}