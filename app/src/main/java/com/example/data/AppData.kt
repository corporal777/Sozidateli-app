package com.example.data

import com.example.data.models.asOptional
import com.example.data.models.user.User
import com.example.data.prefs.AppPrefs
import io.reactivex.subjects.BehaviorSubject

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
            field = value
            if (changed) notificationsCountSubject.onNext(value)
        }

    private var user: User? = null
    var fullUser: User? = null

    val userChangeSubject = BehaviorSubject.createDefault(user.asOptional())
    val tokenChangeSubject = BehaviorSubject.createDefault(token.asOptional())
    val chatMessageCountSubject = BehaviorSubject.createDefault(chatUnreadMessageCount)
    val chatRequestsCountSubject = BehaviorSubject.createDefault(chatRequestsCount)
    val notificationsCountSubject = BehaviorSubject.createDefault(notificationsCount)

    fun setUser(user: User) {
        val changed = this.user != user
        this.user = user
        appPrefs.userId = user.user_id
        if (changed) userChangeSubject.onNext(user.asOptional())
        notificationsCount = user.notification_unread
    }

    fun getUser(): User = user
            ?: throw UninitializedPropertyAccessException("\"User\" was queried before being initialized")
}