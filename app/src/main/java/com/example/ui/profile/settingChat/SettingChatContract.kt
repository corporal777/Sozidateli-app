package com.example.ui.profile.settingChat

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface SettingChatContract {
    interface View : BaseContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSetting(user: User)
    }

    interface Presenter : BaseContract.Presenter{
        fun onChangeSetting(type:String,isEnabled:Boolean)
    }
}
