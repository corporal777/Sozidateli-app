package com.example.ui.search.code

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface EnterCodeContract {
    interface View : BaseContract.View {
        @OneExecution
        fun showEvent(eventId: String)

        @OneExecution
        fun showEventNotFoundError()
    }

    interface Presenter : BaseContract.Presenter {
        fun onSearchClick(code: String)
    }
}
