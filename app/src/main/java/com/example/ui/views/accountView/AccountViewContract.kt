package com.example.ui.views.accountView

import com.arellomobile.mvp.MvpView
import com.example.ui.base.BaseContract

class AccountViewContract {

    interface View : MvpView {
        fun setChatCount(count:Int)
    }

    interface Presenter : BaseContract.Presenter {

    }
}