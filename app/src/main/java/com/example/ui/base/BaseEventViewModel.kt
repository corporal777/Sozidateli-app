package com.example.ui.base

import com.examle.data.AppData
import com.example.data.models.EventNew

abstract class BaseEventViewModel(private val appData: AppData) : BaseViewModel() {

    fun isProfileLevelLow(event: EventNew): Boolean {
        val state = event.binds?.currentUserRegistrationState ?: return true
        return if (state.prohibitions?.profileLevelToLow?.requiredLevel == "basic") !getHasBase()
        else !getHasMax()
    }

    fun getHasBase() = appData.hasBaseState
    fun getHasMax() = appData.hasMaxState
}