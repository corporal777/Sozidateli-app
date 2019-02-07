package com.example.data

import com.example.data.models.Event
import com.example.data.models.user.User
import com.example.data.prefs.AppPrefs

class AppData(
        private val appPrefs: AppPrefs
) {

    private val userChangeListeners = mutableListOf<OnUserChangeListener>()
    private val tokenChangeListeners = mutableListOf<OnTokenChangeListener>()

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
        userChangeListeners.forEach { it.onUserChange(user) }
    }

    fun addOnUserChangeListener(userChangeListener: OnUserChangeListener) {
        if (!userChangeListeners.contains(userChangeListener)) userChangeListeners.add(userChangeListener)
    }

    fun removeOnUserChangeListener(userChangeListener: OnUserChangeListener) {
        userChangeListeners.remove(userChangeListener)
    }

    private fun performOnTokenChange() {
        tokenChangeListeners.forEach { it.onTokenChange(token) }
    }

    fun addOnTokenChangeListener(tokenChangeListener: OnTokenChangeListener) {
        if (!tokenChangeListeners.contains(tokenChangeListener)) tokenChangeListeners.add(tokenChangeListener)
    }

    fun removeOnTokenChangeListener(tokenChangeListener: OnTokenChangeListener) {
        tokenChangeListeners.remove(tokenChangeListener)
    }

    interface OnUserChangeListener {
        fun onUserChange(user: User?)
    }

    interface OnTokenChangeListener {
        fun onTokenChange(token: String?)
    }
}