package com.example.data

import com.examle.data.models.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class UiStateData {

    val showEventAgreement = MutableStateFlow<Boolean>(false)
    val eventAgreementAccepted = MutableSharedFlow<Boolean>()

    fun showEventAgreementDialog(): Flow<Boolean> {
        showEventAgreement.update { true }
        return eventAgreementAccepted
    }
}