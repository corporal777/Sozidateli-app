package com.example.ui.mySchedule.subevent

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Subevent
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface SubeventContract {
    interface View : BaseContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(subevent:Subevent, users:List<User>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSpeakerProfile(user: User)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openUserList(subevent: Subevent)

    }

    interface Presenter : BaseContract.Presenter{
        fun onSpeakerClick(user: User)
        fun onOpenUserListClick()
    }
}
