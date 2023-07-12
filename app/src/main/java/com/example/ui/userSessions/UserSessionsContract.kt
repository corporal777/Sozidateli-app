package com.example.ui.userSessions

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventActivityModel
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.data.models.UserSessions
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter

interface UserSessionsContract {

    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setCurrentSession(session: UserSessionModel, isHasSessions : Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setOtherSessions(sessions: List<UserSessionModel?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSessionsLoadingPlaceholder()

        @StateStrategyType(SkipStrategy::class)
        fun updateKillSessionsButton(isHasSessions : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun killAllSessionsClick()
        fun killUsersDeviceSessionClick(id: Int)
    }
}