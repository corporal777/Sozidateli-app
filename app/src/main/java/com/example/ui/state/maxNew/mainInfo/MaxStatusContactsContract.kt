package com.example.ui.state.maxNew.mainInfo

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.ui.state.maxNew.base.BaseMaxStateContract

interface MaxStatusContactsContract {
    interface View : BaseMaxStateContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPersonalData(user: UserDetail)
    }
    interface Presenter : BaseMaxStateContract.Presenter {
        fun saveContactsClick(data: MutableMap<String, Any?>)
    }
}