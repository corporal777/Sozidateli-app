package com.example.data

import com.example.data.models.ApiResponse
import com.example.data.models.Event
import com.example.data.models.Optional
import com.example.data.models.asOptional
import com.example.data.models.user.User
import com.example.data.prefs.AppPrefs
import io.reactivex.Observable
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

    var event: Event? = null
        set(value) {
            field = value
            appPrefs.selectedEvent = value?.event_id
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
            if (changed) chatUnreadMessageCountSubject.onNext(value)
        }

    var error: ApiResponse<*>? = null
        set(value) {
            field = value
            onErrorHandler.onNext(value.asOptional())
        }

    private var user: User? = null
    var openedNotificationId: String? = null

    private val userChangeSubject = BehaviorSubject.createDefault(user.asOptional())
    val onUserChange: Observable<Optional<User>> = userChangeSubject

    private val tokenChangeSubject = BehaviorSubject.createDefault(token.asOptional())
    val onTokenChange: Observable<Optional<String>> = tokenChangeSubject

    private val chatUnreadMessageCountSubject = BehaviorSubject.createDefault(chatUnreadMessageCount)
    val onChatUnreadMessageCountChange: Observable<Int> = chatUnreadMessageCountSubject

    private val onErrorHandler = PublishSubject.create<Optional<ApiResponse<*>>>()
    val onErrorHandlerListener: Observable<Optional<ApiResponse<*>>> = onErrorHandler

    fun setUser(user: User) {
        val changed = this.user != user
        this.user = user
        appPrefs.userId = user.user_id
        if (changed) userChangeSubject.onNext(user.asOptional())
    }

    fun getUser(): User = user
            ?: throw UninitializedPropertyAccessException("\"User\" was queried before being initialized")
}