package com.example.ui.event

import androidx.lifecycle.viewModelScope
import com.examle.data.models.DataState
import com.examle.domain.interactor.EventInteractor
import com.examle.domain.model.event.EventDetailModel
import com.example.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel
@Inject constructor(
    private val interactor: EventInteractor
) : BaseViewModel() {

    private val _eventDetail = MutableStateFlow<DataState<EventDetailModel>>(DataState.Loading(true))
    val eventDetail: StateFlow<DataState<EventDetailModel>> = _eventDetail.asStateFlow()

    fun getEventDetail(id: String) {
        interactor.getEventDetail(id)
            .onEach { _eventDetail.value = DataState.Success(it) }
            .launchIn(viewModelScope)
    }
}