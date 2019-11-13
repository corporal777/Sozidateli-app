package com.example.ui.agreement

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface UserAgreementContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setContent(content: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setTitle(title: String)
    }

    interface Presenter : BaseContract.Presenter
}
