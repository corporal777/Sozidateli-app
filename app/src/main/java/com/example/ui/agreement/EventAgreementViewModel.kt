package com.example.ui.agreement

import androidx.lifecycle.viewModelScope
import com.examle.domain.interactor.EventInteractor
import com.examle.domain.model.event.EventModel
import com.example.data.UiStateData
import com.example.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventAgreementViewModel
@Inject constructor(
    private val uiStateData: UiStateData,
    private val interactor: EventInteractor
) : BaseViewModel() {

    val eventState = MutableStateFlow<EventModel?>(null)

    fun acceptEventAgreement(event : EventModel){
        viewModelScope.launch {
            interactor.acceptEventAgreement(event)
                .withLoading()
                .catch { it.printStackTrace() }
                .collectLatest { eventState.value = it }
        }
    }
}