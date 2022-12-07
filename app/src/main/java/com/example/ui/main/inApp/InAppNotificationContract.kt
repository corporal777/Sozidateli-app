package com.example.ui.main.inApp

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Notification
import com.example.ui.base.bottomSheet.BaseBottomSheetContract

interface InAppNotificationContract {

    interface View : BaseBottomSheetContract.View{

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setNotification(notification: Notification)


        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutEvent(eventId : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutOrganization(organizationId : String)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
    }
}