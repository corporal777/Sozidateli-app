package com.example.data

import com.examle.data.models.DataState
import com.examle.domain.model.Optional
import com.examle.domain.model.asOptional
import com.examle.domain.model.auth.SnAuthModel
import com.examle.domain.model.event.EventModel
import com.example.common.exceptions.AgreementException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.coroutineContext
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.update

class UiStateData {

    val eventAgreement = MutableSharedFlow<EventModel>()
    lateinit var changedEvent : MutableSharedFlow<EventModel?>

    suspend fun showEventAgreementDialog(event: EventModel): Flow<EventModel> {
        eventAgreement.emit(event.copy())
        return MutableSharedFlow<EventModel?>().apply {
            changedEvent = this
        }
            .map { it ?: throw AgreementException() }
            .distinctUntilChanged()
    }
}