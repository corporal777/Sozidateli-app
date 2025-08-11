package com.example.ui.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.examle.data.AppData
import com.examle.domain.interactor.EventInteractor
import com.examle.domain.model.PaginationResponse
import com.examle.domain.model.event.EventModel
import com.example.common.constants.EVENT_BINDS
import com.example.common.constants.EVENT_LIMIT
import com.example.common.constants.EVENT_OFFSET
import com.example.common.constants.EVENT_PUBLIC
import com.example.common.constants.EVENT_SORT_FIELD
import com.example.common.constants.EVENT_SORT_TYPE
import com.example.common.constants.EVENT_STATUS
import com.example.common.flatMap
import com.example.data.UiStateData
import com.example.extensions.build
import com.example.ui.base.BaseEventViewModel
import com.example.util.paginationNew.PagingSourceFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val appData: AppData,
    private val uiStateData: UiStateData,
    private val interactor: EventInteractor
) : BaseEventViewModel(appData) {

    private val pagination = PagingSourceFactory { limit, offset ->
        getPaginationRequest(limit, offset)
    }.build(initialSize = 20, distance = 2)

    val events = pagination.catch { it.printStackTrace() }.cachedIn(viewModelScope)

    private var eventsList = listOf<EventModel>()

    fun onActionRegister(event: EventModel) {
        if (isProfileLevelLow(event)) return
        else interactor.checkEventAgreement(event)
            .onEach { if (it) updatedEvent.update { event } else registerToEvent(event) }
            .launchIn(viewModelScope)
    }

    fun registerToEvent(event: EventModel) {
        viewModelScope.launch {
            if (event.state?.isFormEnabled == true) flowOf(event)
            else interactor.registerEvent(event.id)
                .flatMap { interactor.getEvent(event) }
                .map { updatePagingData(it) }
                .catch { emit(updatePagingData(event)) }
                .withProgressLoading(appData._progressLoading)
                .collectLatest {
                    pagination.invalidateFrom(it)
                }
        }
    }

    fun onActionCancel(event: EventModel) {
        val registrationId = event.userRegistration?.id ?: 0
        if (isProfileLevelLow(event)) return
        else viewModelScope.launch {
            interactor.cancelRegisterEvent(registrationId)
                .flatMap { interactor.getEvent(event) }
                .map { updatePagingData(it) }
                .catch { it.printStackTrace() }
                .withProgressLoading(appData._progressLoading)
                .collectLatest {
                    pagination.invalidateFrom(it)
                }
        }
    }

    fun setEventsLocal(list: List<EventModel>) {
        eventsList = list
    }

    private fun updatePagingData(event: EventModel): List<EventModel> {
        return eventsList.map { pagingEvent ->
            if (pagingEvent.id == event.id) event
            else pagingEvent
        }
    }

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
                EVENT_BINDS to "current-user-registration,current-user-registration-state",
                EVENT_PUBLIC to "true",
                EVENT_STATUS to "approved,registration,registrationFinished,running"
            )
        )
    }
}