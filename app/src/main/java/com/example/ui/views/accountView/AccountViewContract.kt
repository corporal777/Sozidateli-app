package com.example.ui.views.accountView

import com.example.ui.base.BaseContract

class AccountViewContract {

    interface View : BaseContract.View {
        fun setChatCount(count:Int)
    }

    interface Presenter : BaseContract.Presenter {

    }
}