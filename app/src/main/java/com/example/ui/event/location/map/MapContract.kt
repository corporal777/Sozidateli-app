package com.example.ui.event.location.map

import com.example.ui.base.bottomSheet.BaseBSContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface MapContract {

    interface View : BaseBSContract.View {

        @OneExecution
        fun initializeMap()

        @AddToEndSingle
        fun showContent()

        @OneExecution
        fun setMarker(lat: Double, lon: Double)

        @AddToEndSingle
        fun setDescription(title: String?, description: String?)

        @OneExecution
        fun shareUrl(url: String)

        @OneExecution
        fun openUrl(url: String)
    }

    interface Presenter : BaseBSContract.Presenter {
        fun onMapReady()
        fun onShareClick()
        fun onOpenRouteClick()
    }

}