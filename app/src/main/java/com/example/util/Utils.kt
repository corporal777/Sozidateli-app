package com.example.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.view.Menu
import androidx.core.app.NotificationCompat
import bundleOf
import com.example.R
import com.example.data.models.LocalNotification
import com.example.data.models.UserChat
import com.example.ui.main.MainActivity
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Observable
import performOnMain
import java.lang.Exception
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.content.ComponentName
import android.app.ActivityManager
import android.content.res.Resources
import androidx.core.app.NotificationCompat.GROUP_ALERT_SUMMARY
import com.example.App
import com.example.data.models.ProfileField
import com.example.data.models.Type


object Utils {

    val defaultDateFormatter: DateFormat
        get() = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault())

    val defaultServerDateFormatter: DateFormat
        get() = SimpleDateFormat(DATE_FORMAT_SERVER_TIMESTAMP, Locale.getDefault())

    fun formatToDefaultDate(serverTimestamp: String): String? {
        val serverDate = try {
            defaultServerDateFormatter.parse(serverTimestamp)
        } catch (e: ParseException) {
            return null
        }

        return defaultDateFormatter.format(serverDate)
    }

    fun getDatesInterval(startDate: String?, finishDate: String?): String {
        return getDatesInterval(if (startDate == null) 0 else defaultServerDateFormatter.parse(startDate).time, if (finishDate == null) 0 else defaultServerDateFormatter.parse(finishDate).time)
    }

    fun getDatesInterval(startDate: Long, finishDate: Long): String {
        val start = Calendar.getInstance().apply { timeInMillis = startDate }
        val finish = Calendar.getInstance().apply { timeInMillis = finishDate }

        val startFormat = if (start.get(Calendar.YEAR) == finish.get(Calendar.YEAR)) DATE_FORMAT_SHORT_MONTH_NO_YEAR else DATE_FORMAT_SHORT_MONTH_FULL_YEAR
        val formattedStart = SimpleDateFormat(startFormat, Locale.getDefault()).format(start.time)
        var formattedFinish = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault()).format(finish.time)

        if (finishDate == 0L) formattedFinish = "н.в"

        return "$formattedStart - $formattedFinish"
    }

    fun processMainMenu(menu: Menu, onChatClick: () -> Unit, onAccountClick: () -> Unit) {
        val chatItem = menu.findItem(R.id.chat)
        val chatView = chatItem?.actionView as ChatView?
        chatView?.run { setOnClickListener { onChatClick() } }

        val accountItem = menu.findItem(R.id.account)
        val accountView = accountItem?.actionView as AccountView?
        accountView?.run { setOnClickListener { onAccountClick() } }
    }

    fun showLocalChatNotification(context: Context, localNotification: LocalNotification) {
        showNotification(context, localNotification.avatar, localNotification.chatId, localNotification.text, localNotification.senderId, localNotification.userName, localNotification.messageId)

    }

    fun showPushChatNotification(context: Context, userChat: UserChat?) {
        showNotification(context, userChat?.userSender?.user_avatar, userChat?.id?.toString(), userChat?.lastMessage, userChat?.userSender?.user_id
                ?: 0, userChat?.userSender?.fullName, userChat?.messageId)
    }

    private fun showNotification(context: Context, avatar: String?, chatId: String?, text: String?, senderId: Int, senderName: String?, notificationId: String?) {
        if (context.applicationContext is App) {
            val openedChatId = (context.applicationContext as App).currentChatID

            openedChatId?.let {
                if (it == chatId) return
            }
        }

        Observable.fromCallable {
            if (avatar.isNullOrEmpty()) {
                createChatNotifiaction(context, null, chatId, text, senderId, senderName, notificationId)
                return@fromCallable
            }
            Picasso.get().load(avatar.let { if (it.isNullOrEmpty()) null else it })
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(CropCircleTransformation())
                    .into(object : Target {
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            createChatNotifiaction(context, null, chatId, text, senderId, senderName, notificationId)
                        }

                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            createChatNotifiaction(context, bitmap, chatId, text, senderId, senderName, notificationId)
                        }
                    })

        }.performOnMain().subscribe()
    }

    private fun createChatNotifiaction(context: Context, bitmap: Bitmap?, chatId: String?, text: String?, senderId: Int, senderName: String?, notificationId: String?) {
        val channelId = context.getString(R.string.app_name)

        val summaryNotification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setGroup(chatId)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(createNotificationIntent(context, chatId, senderId, senderName, notificationId))
                .setSound(null)
                .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                .setGroupSummary(true)
                .build()

        NotificationCompat.Builder(context, channelId)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(senderName)
                .setContentText(text)
                .setAutoCancel(true)
                .setTicker(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(createNotificationIntent(context, chatId, senderId, senderName, notificationId))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(bitmap)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setGroup(chatId)
                .apply {
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        notificationManager.notify(chatId!!.toInt(), summaryNotification)
                    }
                    notificationManager.notify(notificationId.hashCode()
                            ?: 0, this.build())
                }
    }

    private fun createNotificationIntent(context: Context, chatId: String?, senderId: Int, senderName: String?, notificationId: String?): PendingIntent {

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(FIELD_CHAT, bundleOf(
                    FIELD_CHAT_ID to chatId,
                    FIELD_SENDER_ID to senderId.toString(),
                    FIELD_LABEL to senderName,
                    FIELD_NOTIFICATION_ID to notificationId
            ))
        }

        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    public fun getDataByName(obj: Any?, fieldName: String): Any? {
        if (obj == null) return null
        try {
            val field = obj.javaClass.getDeclaredField(fieldName)
            field.setAccessible(true)
            val value = field.get(obj)
            return value
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    public fun getListFieldValueByMapDefault(obj: Any?, default: MutableList<ProfileField>): MutableList<ProfileField> {
        val result = mutableListOf<ProfileField>()
        default.forEach { default ->
            result.add(ProfileField(default.type, default.nameField, default.label,default.required, getDataByName(obj, default.nameField)))
        }
        return result
    }

    fun dpToPx(dp:Int):Int{
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}