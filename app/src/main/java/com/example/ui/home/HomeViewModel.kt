package com.example.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.examle.data.AppData
import com.examle.domain.interactor.EventInteractor
import com.examle.domain.model.EventModel
import com.examle.domain.model.PaginationResponse
import com.example.common.EVENT_BINDS
import com.example.common.EVENT_LIMIT
import com.example.common.EVENT_OFFSET
import com.example.common.EVENT_PUBLIC
import com.example.common.EVENT_SORT_FIELD
import com.example.common.EVENT_SORT_TYPE
import com.example.common.EVENT_STATUS
import com.example.extensions.build
import com.example.ui.base.BaseEventViewModel
import com.example.util.paginationNew.PagingSourceFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val appData: AppData,
    private val interactor: EventInteractor
) : BaseEventViewModel(appData) {

    private val pagination = PagingSourceFactory { limit, offset ->
        getPaginationRequest(limit, offset)
    }.build(initialSize = 20, distance = 2)


    val events = pagination.catch { it.printStackTrace() }.cachedIn(viewModelScope)

    private val _updatedEvent = MutableStateFlow<EventModel?>(null)
    val updatedEvent: StateFlow<EventModel?> = _updatedEvent

    private var eventsList = listOf<EventModel>()


    fun onActionRegister(event: EventModel, withAccept: Boolean) {
//        if (isProfileLevelLow(event)) {
//
//        } else viewModelScope.launch {
//            eventRepository.checkEventAgreement(event, withAccept)
//                .onEach { e -> eventsList.find { it.id == e.id }?.state?.setAccepted(e) }
//                .flatMap { flow { emit(eventRepository.registerEvent(event.id ?: 0)) } }
//                .flatMap { eventRepository.getEventFlow(event.id.toString()) }
//                .map { updatePagingData(it) }
//                .catch { emit(eventsList) }
//                .withProgressLoading(appData._progressLoading)
//                .collectLatest {
//                    pagination.invalidateFrom(it)
//                }
//        }
    }

    fun onActionCancel(event: EventModel) {
//        val registrationId = event.binds?.currentUserRegistration?.id ?: 0
//        if (isProfileLevelLow(event)) {
//
//        } else viewModelScope.launch {
//            flow { emit(eventRepository.cancelRegisterEvent(registrationId)) }
//                .flatMap { eventRepository.getEventFlow(event.id.toString()) }
//                .map { updatePagingData(it) }
//                .catch { it.printStackTrace() }
//                .withProgressLoading(appData._progressLoading)
//                .collectLatest {
//                    pagination.invalidateFrom(it)
//                }
//        }
    }

    fun setEventsLocal(list: List<EventModel>) {
        eventsList = list
    }

//    private fun updatePagingData(event: EventModel): List<EventModel> {
//        return eventsList.map { pagingEvent ->
//            if (pagingEvent.id == event.id) pagingEvent.setFieldsForAction(event)
//            else pagingEvent
//        }
//    }

    private suspend fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): PaginationResponse<EventModel> {
        return interactor.getEventsList(
            mapOf(
                EVENT_LIMIT to limit,
                EVENT_OFFSET to offset,
                EVENT_SORT_TYPE to "desc",
                EVENT_SORT_FIELD to "id",
                EVENT_BINDS to "current-user-registration,current-user-registration-state,eventRegistrationState",
                EVENT_PUBLIC to "true",
                EVENT_STATUS to "approved,registration,registrationFinished,running"
            )
        )
    }
}