package com.example.data

import com.example.data.models.*
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
            if (changed) {
                if (value.isNullOrEmpty()) {
                    field = null
                    appPrefs.userToken = null
                    if (!isLoggedOut) {
                        logout()
                    }

                    tokenChangeSubject.onNext(Optional())
                } else if (!isLoggedOut) {
                    field = value
                    appPrefs.userToken = value
                    tokenChangeSubject.onNext(value.asOptional())
                }
            }
        }

    var isStoriesShown: Boolean = appPrefs.isStoriesShown
        set(value) {
            val changed = field != value
            field = value
            if (changed) {
                appPrefs.isStoriesShown = value
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

    var isLoggedOut = token.isNullOrEmpty()
        private set

    val userChangeSubject = BehaviorSubject.createDefault(user.asOptional())
    val tokenChangeSubject = BehaviorSubject.createDefault(token.asOptional())
    val chatMessageCountSubject = BehaviorSubject.createDefault(chatUnreadMessageCount)
    val chatRequestsCountSubject = BehaviorSubject.createDefault(chatRequestsCount)
    val notificationsCountSubject = BehaviorSubject.createDefault(notificationsCount)
    val notificationReadSubject = PublishSubject.create<Pair<Int, Notification.AcceptState>>()
    val userPhoneConfirmedSubject = BehaviorSubject.createDefault(false)

    fun setUser(user: User) {
        val changed = this.user != user
        this.user = user
        appPrefs.userId = user.user_id
        if (changed) userChangeSubject.onNext(user.asOptional())
        notificationsCount = user.notification_unread
    }

    fun setUserShort(userShort: UserShort) {
        setUser(userShort.toUser())
    }

    fun getUser(): User = user
            ?: throw UninitializedPropertyAccessException("\"User\" was queried before being initialized")

    fun updateUser(update: User.() -> Unit) {
        userChangeSubject.onNext(getUser().apply(update).asOptional())
    }

    fun login(token: String) {
        isLoggedOut = false
        this.token = token
    }

    fun logout() {
        isLoggedOut = true
        user = null
        appPrefs.userId = -1
        notificationsCount = 0
        chatRequestsCount = 0
        chatUnreadMessageCount = 0
        userChangeSubject.onNext(Optional(null))
        token = null
    }
}