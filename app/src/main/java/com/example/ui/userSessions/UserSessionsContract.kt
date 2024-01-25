package com.example.ui.userSessions

import com.example.data.models.UserSessionModel
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserSessionsContract {

    interface View : BaseContract.View {
        @OneExecution
        fun setCurrentSession(session: UserSessionModel, isHasSessions : Boolean)

        @OneExecution
        fun setOtherSessions(sessions: List<UserSessionModel?>)

        @Skip
        fun updateKillSessionsButton(isHasSessions : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onKillSessionsClick()
        fun onKillSessionClick(id: Long)
    }
}