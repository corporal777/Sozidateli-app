package com.example.ui.event.location.map.redesign

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface MapContractNew {

    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun initializeMap()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun showContent()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableCurrentLocation(enable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setMarker(lat: Double, lon: Double)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setDescription(title: String?, description: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun shareUrl(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openUrl(url: String)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        fun onMapReady()
        fun onShareClick()
        fun onOpenRouteClick()
    }

}