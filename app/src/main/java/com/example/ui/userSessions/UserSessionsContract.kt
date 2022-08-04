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
import com.example.ui.userSessions.items.SessionsAction
import com.example.util.pagination.PaginationListGroupAdapter

interface UserSessionsContract {

    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setOtherSessions(sessions: List<UserSessionModel?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setCurrentSession(session: UserSessionModel?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSessionsLoadingPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateSessionsActionButton(action: SessionsAction)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSessionsActionButton(action: SessionsAction)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun hideSessionsActionButton()

        @StateStrategyType(SkipStrategy::class)
        fun showSessionBottomSheetDialog(session: UserSessionModel)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        fun killAllSessionsClick()
        fun killUsersDeviceSessionClick(id: Int)
        fun showSessionClick(session: UserSessionModel)
        fun showOrHideSessionsHistoryClick(action: SessionsAction)
    }
}