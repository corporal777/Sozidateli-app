package com.example.ui.main.inApp

import com.example.data.models.Notification
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface InAppNotificationContract {
    interface View : BaseBottomSheetContract.View{

        @AddToEndSingle
        fun setNotifications(notifications: List<Notification>)

        @OneExecution
        fun showAboutEvent(eventId : String)

        @OneExecution
        fun showAboutOrganization(organizationId : String)

        @OneExecution
        fun showUrl(url: String)

        @OneExecution
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