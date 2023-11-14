package com.example.ui.userprofile.base

import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle

interface BaseUserProfileContract {
    interface View : BaseContract.View {

        @AddToEndSingle
        fun setUserData(user: UserDetail, state: String)
    }

    interface Presenter : BaseContract.Presenter
}
