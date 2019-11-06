package com.example.data

import com.example.data.models.Interest
import com.example.data.models.Notification
import com.example.data.models.Optional
import com.example.data.models.asOptional
import com.example.data.models.user.User
import com.example.data.prefs.AppPrefs
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class AppData(
        private val appPrefs: AppPrefs
) {

    var token: String? = appPrefs.userToken
        set(value) {
            val changed = field != value
            field = value
            if (changed) {
                appPrefs.userToken = value
                tokenChangeSubject.onNext(value.asOptional())
            }
        }

    var isSubscribedToPush: Boolean = appPrefs.isFCMTokenSent
        set(value) {
            field = value
            appPrefs.isFCMTokenSent = value
        }

    var chatUnreadMessageCount = 0
        set(value) {
            val changed = field != value
            field = value
            if (changed) chatMessageCountSubject.onNext(value)
        }

    var chatRequestsCount = 0
        set(value) {
            val changed = field != value
            field = value
            if (changed) chatRequestsCountSubject.onNext(value)
        }

    var notificationsCount = 0
        set(value) {
            val changed = field != value
            field = if (value < 0) 0 else value
            if (changed) notificationsCountSubject.onNext(value)
        }

    var interests: List<Interest>? = null

    private var user: User? = null

    val userChangeSubject = BehaviorSubject.createDefault(user.asOptional())
    val tokenChangeSubject = BehaviorSubject.createDefault(token.asOptional())
    val chatMessageCountSubject = BehaviorSubject.createDefault(chatUnreadMessageCount)
    val chatRequestsCountSubject = BehaviorSubject.createDefault(chatRequestsCount)
    val notificationsCountSubject = BehaviorSubject.createDefault(notificationsCount)
    val notificationReadSubject = PublishSubject.create<Pair<Int, Notification.AcceptState>>()

    fun setUser(user: User) {
        val changed = this.user != user
        this.user = user
        appPrefs.userId = user.user_id
        if (changed) userChangeSubject.onNext(user.asOptional())
        notificationsCount = user.notification_unread
    }

    fun getUser(): User = user
            ?: throw UninitializedPropertyAccessException("\"UserShort\" was queried before being initialized")

    fun logout() {
        user = null
        appPrefs.userId = -1
        notificationsCount = 0
        chatRequestsCount = 0
        chatUnreadMessageCount = 0
        userChangeSubject.onNext(Optional(null))
        token = null
    }
}