package com.example.data

import com.example.data.models.Event
import com.example.data.models.Optional
import com.example.data.models.asOptional
import com.example.data.models.user.User
import com.example.data.prefs.AppPrefs
import io.reactivex.subjects.PublishSubject

class AppData(
        private val appPrefs: AppPrefs
) {

    private val userChangeSubject = PublishSubject.create<Optional<User>>()
    val onUserChange = userChangeSubject.publish().autoConnect()

    private val tokenChangeSubject = PublishSubject.create<Optional<String>>()
    val onTokenChange = tokenChangeSubject.publish().autoConnect()

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