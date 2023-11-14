package com.example.ui.views.accountView

import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

class AccountViewContract {

    interface View : MvpView {
        @AddToEndSingle
        fun showCounter(show: Boolean)

        @AddToEndSingle
        fun setAvatar(url: String?)
    }

    interface Presenter : BaseContract.Presenter {

        var canShowBadge: Boolean
    }
}