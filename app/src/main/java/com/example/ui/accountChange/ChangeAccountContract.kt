package com.example.ui.accountChange

import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.OneExecution

interface ChangeAccountContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setAccounts(canShow : Boolean, sessions: List<UserSessionModel?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUnLoggedAccounts(canShow : Boolean, sessions: List<UserSessionModel>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setLoginToAnotherAccountButton()

        @StateStrategyType(SkipStrategy::class)
        fun showMessage(message : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAuthorizationFragment()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLoginFragment(login : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showBrowser(url : String)
    }

    interface Presenter : BaseContract.Presenter {
        fun logoutFromAccount(session: UserSessionModel)
        fun switchAccount(session: UserSessionModel)
        fun authToAccountClick()
        fun loginToAccountClick(user : UserDetail)
        fun onClickClose()
    }
}