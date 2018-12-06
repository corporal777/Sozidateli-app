package com.example.ui.aboutForum

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.ui.base.BaseContract

interface AboutForumContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(event: Event)
    }

    interface Presenter : BaseContract.Presenter
}
