package com.example.ui.event

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.examle.data.AppData
import com.examle.data.models.DataState
import com.examle.data.models.ResponseState
import com.examle.domain.interactor.EventDetailInteractor
import com.examle.domain.interactor.EventInteractor
import com.examle.domain.model.event.EventModel
import com.example.common.flatMap
import com.example.data.UiStateData
import com.example.ui.base.BaseEventViewModel
import com.example.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel
@Inject constructor(
    private val appData: AppData,
    private val uiState: UiStateData,
    private val interactor: EventDetailInteractor
) : BaseViewModel(appData) {

    private val _eventDetail = MutableStateFlow<DataState<EventModel>>(DataState.Loading(true))
    val eventDetail: StateFlow<DataState<EventModel>> = _eventDetail.asStateFlow()


    fun getEventDetail(id: String) {
        interactor.getEventDetail(id)
            .onEach { _eventDetail.value = DataState.Success(it) }
            .launchIn(viewModelScope)
    }

    fun addOrRemoveEventFavorite(event: EventModel) {
        viewModelScope.launch {
            interactor.addOrRemoveEventFavorite(event)
                .withProgressLoading(appData._progressLoading)
                .catch { it.printStackTrace() }
                .collectLatest {
                    _eventDetail.value = DataState.Success(it)
                }
        }
    }

    fun onActionRegister(event: EventModel) {
        if (isProfileLevelLow(event)) return
        else viewModelScope.launch {
            interactor.checkEventAgreement(event.state)
                .flatMap {
                    if (it) uiState.showEventAgreementDialog(event)
                    else flowOf(event)
                }
                .catch { it.printStackTrace() }
                .collectLatest { registerToEvent(it) }
        }
    }

    private fun registerToEvent(event: EventModel) {
        viewModelScope.launch {
            if (event.state?.isFormEnabled == true) flowOf(event)
            else interactor.registerEvent(event.id)
                .flatMap { interactor.getEventDetail(event.id.toString()) }
                .catch { it.printStackTrace() }
                .withLoading()
                .collectLatest {
                    _eventDetail.value = DataState.Success(it)
                }
        }
    }

    fun onActionCancel(event: EventModel) {
        val registrationId = event.userRegistration?.id ?: 0
        if (isProfileLevelLow(event)) return
        else viewModelScope.launch {
            interactor.cancelRegisterEvent(registrationId)
                .flatMap { interactor.getEventDetail(event.id.toString()) }
                .catch { it.printStackTrace() }
                .withLoading()
                .collectLatest {
                    _eventDetail.value = DataState.Success(it)
                }
        }
    }
}