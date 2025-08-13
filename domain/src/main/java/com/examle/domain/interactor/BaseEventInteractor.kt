package com.examle.domain.interactor

import com.examle.domain.model.event.EventModel
import com.examle.domain.model.event.EventStateModel
import com.examle.domain.repository.EventRepository
import com.example.common.exceptions.AgreementException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

open class BaseEventInteractor(private val repository: EventRepository) {

    open fun checkEventAgreement(state: EventStateModel?): Flow<Boolean> {
        return if (state?.userAgreement.isNullOrEmpty()) flowOf(false)
        else if (state?.agreementState == "accepted") flowOf(false)
        else flowOf(true)
    }

    open fun acceptEventAgreement(event: EventModel): Flow<EventModel> {
        return repository.acceptEventAgreement(event.id)
            .onEach {
                if (it == "has already been taken") throw AgreementException()
                else event.state?.agreementState = it
            }.map { event }
    }

    open fun registerEvent(id: Int?): Flow<Unit> {
        return flow { emit(repository.registerEvent(id ?: 0)) }
    }

    open fun cancelRegisterEvent(id: Int?): Flow<Unit> {
        return flow { emit(repository.cancelRegisterEvent(id ?: 0)) }
    }

}