package com.example.ui.event.location.map

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface MapContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun initializeMap()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showContent()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableCurrentLocation(enable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setMarker(lat: Double, lon: Double)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setDescription(title: String?, description: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun shareUrl(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openUrl(url: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onMapReady()
        fun onShareClick()
        fun onOpenRouteClick()
    }
}
