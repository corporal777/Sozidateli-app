package com.example.ui.base

import com.examle.data.AppData
import com.examle.domain.model.event.EventModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseEventViewModel(private val appData: AppData) : BaseViewModel() {

    val updatedEvent = MutableStateFlow<EventModel?>(null)


    fun isProfileLevelLow(event: EventModel): Boolean {
        val state = event.userRegistrationState ?: return true
        return if (state.requiredLevel == "basic") !getHasBase()
        else !getHasMax()
    }

    fun getHasBase() = appData.hasBaseState
    fun getHasMax() = appData.hasMaxState
}