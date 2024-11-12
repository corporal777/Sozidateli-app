package com.example.ui.agreement

import com.example.data.models.EventNew
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserAgreementBottomSheetContract {
    interface View : MvpView {

        @OneExecution
        fun setAcceptAgreement(isAccept : Boolean, eventNew: EventNew)

        @Skip
        fun showCustomLoading(show : Boolean)
    }

    interface Presenter {
        fun acceptAgreement(eventNew: EventNew)
        fun notAcceptAgreement(eventNew: EventNew)
    }
}