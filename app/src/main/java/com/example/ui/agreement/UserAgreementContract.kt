package com.example.ui.agreement

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle

interface UserAgreementContract {
    interface View : BaseContract.View {
        @AddToEndSingle
        fun setContent(content: String)

        @AddToEndSingle
        fun setTitle(title: String)
    }

    interface Presenter : BaseContract.Presenter
}
