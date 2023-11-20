package com.example.ui.snAuth

import android.content.Context
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK

class SnAuthManager(private val context: Context) {

    private val authListeners = mutableListOf<OnSnAuthListener>()

    fun addOnSnAuthListener(snAuthListener: OnSnAuthListener) {
        if (!authListeners.contains(snAuthListener)) authListeners.add(snAuthListener)
    }

    fun removeOnSnAuthListener(snAuthListener: OnSnAuthListener) {
        authListeners.remove(snAuthListener)
    }

    fun startAuthVk(scopes: Array<String>? = null) = start(SnType.VK, scopes)

    private fun start(snType: SnType, vkScopes: Array<String>? = null) {
        context.startActivity(SnAuthActivity.getStartIntent(context, snType, vkScopes).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        })
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