package com.example.ui.auth.snAuth

import android.content.Context
import com.example.data.models.DataState
import com.example.data.models.SnAuth
import com.example.data.models.SnType
import io.reactivex.subjects.SingleSubject
import kotlinx.coroutines.flow.MutableSharedFlow

object SnAuthCallbackHelper {

    private lateinit var authRequest : SingleSubject<SnAuth>
    private lateinit var flowRequest : MutableSharedFlow<DataState<SnAuth>>

    fun createRequest(): SingleSubject<SnAuth> {
        return SingleSubject.create<SnAuth>().apply {
            authRequest = this
        }
    }

    fun createRequestFlow(): MutableSharedFlow<DataState<SnAuth>> {
        return MutableSharedFlow<DataState<SnAuth>>().apply {
            flowRequest = this
        }
    }

    fun getRequestFlow(): MutableSharedFlow<DataState<SnAuth>> {
        return flowRequest
    }

    fun getRequest(): SingleSubject<SnAuth> {
        return authRequest
    }

    fun start(context : Context, snType: SnType): MutableSharedFlow<DataState<SnAuth>> {
        return SnAuthActivity.createStartRequest(context, snType)
    }
}