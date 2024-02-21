package com.example.ui.event.formResult

import com.example.data.models.EventRegisterFieldData
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EventFormResultContract {

    interface View : BaseBottomSheetContract.View {
        @OneExecution
        fun setFormResult(fieldsData: List<EventRegisterFieldData<*>>?)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
    }

}