package com.example.ui.auth.snAuth

import android.content.Context
import com.example.data.models.SnAuth
import com.example.data.models.SnType
import io.reactivex.subjects.SingleSubject

object SnAuthCallbackHelper {

    private lateinit var authRequest : SingleSubject<SnAuth>

    fun createRequest(): SingleSubject<SnAuth> {
        return SingleSubject.create<SnAuth>().apply {
            authRequest = this
        }
    }

    fun getRequest(): SingleSubject<SnAuth> {
        return authRequest
    }

    fun start(context : Context, snType: SnType): SingleSubject<SnAuth> {
        return SnAuthActivity.createStartRequest(context, snType)
    }
}