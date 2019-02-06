package com.example.ui.snAuth

import android.content.Context

object SnAuthManager {

    private val authListeners = mutableListOf<OnSnAuthListener>()

    fun addOnSnAuthListener(snAuthListener: OnSnAuthListener) {
        if (!authListeners.contains(snAuthListener)) authListeners.add(snAuthListener)
    }

    fun removeOnSnAuthListener(snAuthListener: OnSnAuthListener) {
        authListeners.remove(snAuthListener)
    }

    fun startAuthVk(context: Context, scopes: Array<String>? = null) = start(context, SnType.VK, scopes)
    fun startAuthFacebook(context: Context) = start(context, SnType.FB)
    fun startAuthOk(context: Context) = start(context, SnType.OK)

    private fun start(context: Context, snType: SnType, vkScopes: Array<String>? = null) {
        context.startActivity(SnAuthActivity.getStartIntent(context, snType, vkScopes))
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