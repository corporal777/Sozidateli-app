package com.example.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class StatesData {

    val _showEventAgreement = MutableStateFlow<Boolean>(false)
    val _eventAgreementAccepted = MutableSharedFlow<Boolean>()
}