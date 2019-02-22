package com.example.data

import com.example.data.models.Event
import com.example.data.models.Optional
import com.example.data.models.asOptional
import com.example.data.models.user.User
import com.example.data.prefs.AppPrefs
import io.reactivex.Observable
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
                performOnTokenChange()
            }
        }

    var event: Event? = null
        set(value) {
            field = value
            appPrefs.selectedEvent = value?.id
        }

    var isSubscribedToPush: Boolean = appPrefs.isFCMTokenSent
        set(value) {
            field = value
            appPrefs.isFCMTokenSent = value
        }

    private var user: User? = null
    var openedNotificationId:String?=null

    private val userChangeSubject = BehaviorSubject.createDefault(user.asOptional())
    val onUserChange: Observable<Optional<User>> = userChangeSubject

    private val tokenChangeSubject = BehaviorSubject.createDefault(token.asOptional())
    val onTokenChange: Observable<Optional<String>> = tokenChangeSubject


    fun setUser(user: User) {
        val changed = this.user != user
        this.user = user
        if (changed) performOnUserChange()
    }

    fun getUser(): User = user
            ?: throw UninitializedPropertyAccessException("\"User\" was queried before being initialized")

    private fun performOnUserChange() {
        userChangeSubject.onNext(user.asOptional())
    }

    private fun performOnTokenChange() {
        tokenChangeSubject.onNext(token.asOptional())
    }
}