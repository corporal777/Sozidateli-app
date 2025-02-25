package com.example.ui.accountChange

import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChangeAccountContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setAccounts(canShow : Boolean, sessions: List<UserSessionModel?>)

        @OneExecution
        fun setUnLoggedAccounts(canShow : Boolean, sessions: List<UserSessionModel>)

        @OneExecution
        fun setLoginToAnotherAccountButton()

        @OneExecution
        fun showAuthorizationFragment()

        @OneExecution
        fun showLoginFragment(login : String)
    }

    interface Presenter : BaseContract.Presenter {
        fun logoutFromAccount(session: UserSessionModel)
        fun switchAccount(session: UserSessionModel)
        fun authToAccountClick()
        fun loginToAccountClick(user : UserDetail)
        fun onClickClose()
    }
}