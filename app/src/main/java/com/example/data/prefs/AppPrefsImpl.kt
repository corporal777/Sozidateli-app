package com.example.data.prefs

import android.annotation.SuppressLint
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPrefsImpl @Inject constructor(context: Context) : AppPrefs {

    private val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    override var selectedEvent: String?
        get() = prefs.getString(SELECTED_EVENT, null)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putString(SELECTED_EVENT, value).commit()
        }


    override var isFCMTokenSent: Boolean
        get() = prefs.getBoolean(FCM_TOKEN_SENT, false)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putBoolean(FCM_TOKEN_SENT, value).commit()
        }

    override var userId: Int
        get() = prefs.getInt(USER_ID, -1)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putInt(USER_ID, value).commit()
        }


    override var isStoriesShown: Boolean
        get() = prefs.getBoolean(STORIES, false)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putBoolean(STORIES, value).commit()
        }

    override var userToken: String?
        get() = prefs.getString(USER_TOKEN, null)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putString(USER_TOKEN, value).commit()
        }

    override var uniqueDeviceId: String?
        get() = prefs.getString(USER_DEVICE_ID, null)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putString(USER_DEVICE_ID, value).commit()
        }

    override var attemptsOfChangePassword: Int
        get() = prefs.getInt(USER_ATTEMPTS, 0)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putInt(USER_ATTEMPTS, value).commit()
        }

    override var updateTime: Long
        get() = prefs.getLong(UPDATE_TIME, 0)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putLong(UPDATE_TIME, value).commit()
        }



    companion object {
        const val SELECTED_EVENT = "selected_event"
        const val USER_TOKEN = "user_token"
        const val USER_DEVICE_ID = "user_device"
        const val USER_ATTEMPTS = "user_attempts"
        const val FCM_TOKEN_SENT = "fcm_token_sent"
        const val USER_ID = "user_id"
        const val STORIES = "stories"
        const val UPDATE_TIME = "update_time"
    }
}
