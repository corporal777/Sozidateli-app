package com.example.ui.event.contacts

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EmailAffiliation
import com.example.data.models.MapInfo
import com.example.data.models.PhoneAffiliation
import com.example.ui.base.BaseContract

interface EventContactsContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(phones: List<PhoneAffiliation>, emails: List<EmailAffiliation>, webLinks: List<String>, socialLinks: List<String>, address: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showMap(eventName: String, mapInfo: MapInfo?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onShowOnMapClick()
    }
}