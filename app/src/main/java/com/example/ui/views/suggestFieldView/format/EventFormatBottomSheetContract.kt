package com.example.ui.views.suggestFieldView.format

import com.example.data.models.NewEventFormat
import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

class EventFormatBottomSheetContract {

    interface View : MvpView {
        @OneExecution
        fun setFormats(list: List<NewEventFormat>)

        @Skip
        fun performOnItemSelected(item: NewEventFormat?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onFormatChange(format: String)
        fun onFormatSelected(format: String?)
    }
}