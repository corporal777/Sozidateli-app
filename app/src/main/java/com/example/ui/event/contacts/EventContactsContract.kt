package com.example.ui.event.contacts

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.base.BaseContract

interface EventContactsContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(
                phones: List<EventPhoneModel>,
                emails: List<EventPhoneModel>,
                webLinks: List<String>,
                socialLinks: List<String>,
                address: String?,
                place: String?,
                mapInfo: MapInfo?,
                places: Array<Place>?
        )

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun shareUrl(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openUrl(url: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onShareClick()
        fun onOpenRouteClick()
        fun onOpenAddressClick()
    }
}