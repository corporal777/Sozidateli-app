package com.example.ui.notification.center.redesign.types.system

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.notification.center.redesign.types.base.BaseNotificationTypeContract
import com.example.ui.state.maxNew.base.BaseMaxStateContract

interface SystemNotificationsContract {
    interface View : BaseNotificationTypeContract.View {
    }

    interface Presenter : BaseNotificationTypeContract.Presenter {
        fun onNotificationReadClick(id: Int)
    }
}