package com.example.ui.aboutEvent

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.Partner
import com.example.ui.base.BaseContract

interface AboutEventContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setEventData(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutForum(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showNews(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showDocuments(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showContacts(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showTransfer(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showPartner(partner:Partner)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setLabel(label: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: Event)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setVisibleButtonGoToEvent(isVisible:Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setPartners(partners:List<Partner>)
    }

    interface Presenter : BaseContract.Presenter {
        fun onAboutForumClick()
        fun onNewsClick()
        fun onDocumentsClick()
        fun onContactsClick()
        fun onTransferClick()
        fun onGoToEventClick()
        fun onPartnerClick(partner: Partner)
        fun onImageLoad()
        fun onImageLoadError()
    }
}
