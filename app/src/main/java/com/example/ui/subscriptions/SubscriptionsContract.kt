package com.example.ui.subscriptions

import android.arch.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Subscription
import com.example.ui.base.BaseContract

interface SubscriptionsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(data: PagedList<Subscription>)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSubscriptionClick(subscription: Subscription)
        fun onUnsubscribeClick(subscription: Subscription)
        fun onScrollChange(position: Int, offset: Int)
    }
}
