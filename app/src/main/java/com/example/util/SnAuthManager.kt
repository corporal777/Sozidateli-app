package com.example.util

import android.content.Context
import com.examle.domain.model.auth.SnAuthModel
import com.example.exceptions.SnAuthError

class SnAuthManager(private val context: Context) {

    private val authListeners = mutableListOf<OnSnAuthListener>()

    fun addOnSnAuthListener(snAuthListener: OnSnAuthListener) {
        if (!authListeners.contains(snAuthListener)) authListeners.add(snAuthListener)
    }

    fun removeOnSnAuthListener(snAuthListener: OnSnAuthListener) {
        authListeners.remove(snAuthListener)
    }


    internal fun onSnAuthComplete(snAuth: SnAuthModel) {
        authListeners.forEach { it.onSnAuthComplete(snAuth) }
    }

    internal fun onSnAuthError(error: SnAuthError) {
        authListeners.forEach { it.onSnAuthError(error) }
    }

    interface OnSnAuthListener {
        fun onSnAuthComplete(snAuth: SnAuthModel)
        fun onSnAuthError(error: SnAuthError)
    }
}