package com.example.ui.event.formResult

import com.example.data.models.EventRegisterFieldData
import com.example.ui.base.bottomSheet.BaseBSContract
import moxy.viewstate.strategy.alias.OneExecution

interface EventFormResultContract {

    interface View : BaseBSContract.View {
        @OneExecution
        fun setFormResult(fieldsData: List<EventRegisterFieldData<*>>?)
    }

    interface Presenter : BaseBSContract.Presenter {
    }

}