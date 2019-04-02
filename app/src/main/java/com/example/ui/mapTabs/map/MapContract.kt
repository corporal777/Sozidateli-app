package com.example.ui.mapTabs.map

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface MapContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setMarker(lat: Double, lon: Double)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setDescription(description: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onMapReady()
    }
}
