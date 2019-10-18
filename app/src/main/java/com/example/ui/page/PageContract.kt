package com.example.ui.page

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface PageContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setContent(logo: String?, content: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setTitle(title: String)
    }

    interface Presenter : BaseContract.Presenter
}
