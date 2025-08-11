package com.example.ui.auth.snAuth

import android.content.Context
import com.examle.data.models.DataState
import com.examle.domain.model.auth.SnAuthModel
import com.examle.domain.model.auth.SnType
import kotlinx.coroutines.flow.MutableSharedFlow

object SnAuthCallbackHelper {

    private lateinit var authRequest : MutableSharedFlow<DataState<SnAuthModel>>

    fun createRequest(): MutableSharedFlow<DataState<SnAuthModel>> {
        return MutableSharedFlow<DataState<SnAuthModel>>().apply {
            authRequest = this
        }
    }

    fun getRequest(): MutableSharedFlow<DataState<SnAuthModel>> {
        return authRequest
    }

    fun start(context : Context, snType: SnType): MutableSharedFlow<DataState<SnAuthModel>> {
        return SnAuthActivity.createStartRequest(context, snType)
    }
}