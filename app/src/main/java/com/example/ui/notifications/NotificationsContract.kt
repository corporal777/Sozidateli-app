package com.example.ui.notifications

import android.arch.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.News
import com.example.data.models.Notification
import com.example.ui.base.BaseContract

interface NotificationsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(notifications: PagedList<Notification>)
    }

    interface Presenter : BaseContract.Presenter {
    }
}
