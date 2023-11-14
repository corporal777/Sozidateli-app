package com.example.ui.views.notifications

import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

class NotificationsViewContract {

    interface View : MvpView {
        @AddToEndSingle
        fun showCounter(show: Boolean)
    }

    interface Presenter : BaseContract.Presenter
}