package com.example.ui.partner

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.Partner
import com.example.ui.base.BaseContract

interface PartnerContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun setData(partner: Partner)
    }

    interface Presenter : BaseContract.Presenter
}
