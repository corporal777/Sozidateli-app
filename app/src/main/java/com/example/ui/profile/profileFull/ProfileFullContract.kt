package com.example.ui.profile.profileFull

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface ProfileFullContract {
    interface View : BaseContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(user: User)
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEditProfile()
    }

    interface Presenter : BaseContract.Presenter{
        fun onEditClick()
    }
}
