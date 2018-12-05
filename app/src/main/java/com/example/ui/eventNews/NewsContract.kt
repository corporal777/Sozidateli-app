package com.example.ui.eventNews

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.News
import com.example.ui.base.BaseContract

interface NewsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(news: News)
    }

    interface Presenter : BaseContract.Presenter
}
