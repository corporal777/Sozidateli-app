package com.example.ui.main.inApp

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface InAppNotificationContract {

    interface View : BaseBottomSheetContract.View{

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setNotifications(notifications: List<Notification>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutEvent(eventId : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutOrganization(organizationId : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUrl(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun onNotificationNeedUpdate(notification: Notification)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun onNotificationUrlClick(url: String)
        fun onNotificationReadClick(id: Int)
        fun onNotificationAcceptClick(notification: Notification)
        fun onNotificationCancelClick(notification: Notification)
        fun onNotificationRateClick(eventId: String)
    }
}