package com.example.util

import android.content.Context
import com.example.exceptions.SnAuthError
import com.example.data.models.SnAuth

class SnAuthManager(private val context: Context) {

    private val authListeners = mutableListOf<OnSnAuthListener>()

    fun addOnSnAuthListener(snAuthListener: OnSnAuthListener) {
        if (!authListeners.contains(snAuthListener)) authListeners.add(snAuthListener)
    }

    fun removeOnSnAuthListener(snAuthListener: OnSnAuthListener) {
        authListeners.remove(snAuthListener)
    }


    internal fun onSnAuthComplete(snAuth: SnAuth) {
        authListeners.forEach { it.onSnAuthComplete(snAuth) }
    }

    internal fun onSnAuthError(error: SnAuthError) {
        authListeners.forEach { it.onSnAuthError(error) }
    }

    interface OnSnAuthListener {
        fun onSnAuthComplete(snAuth: SnAuth)
        fun onSnAuthError(error: SnAuthError)
    }
}