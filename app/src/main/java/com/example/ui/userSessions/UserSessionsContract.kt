package com.example.ui.userSessions

import com.example.data.models.UserSessionModel
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface UserSessionsContract {

    interface View : BaseContract.View {
        @AddToEndSingle
        fun setCurrentSession(session: UserSessionModel, isHasSessions : Boolean)

        @AddToEndSingle
        fun setOtherSessions(sessions: List<UserSessionModel?>)

        @AddToEndSingle
        fun showSessionsLoadingPlaceholder()

        @Skip
        fun updateKillSessionsButton(isHasSessions : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun killAllSessionsClick()
        fun killUsersDeviceSessionClick(id: Int)
    }
}