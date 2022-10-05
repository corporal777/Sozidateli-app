package com.example.ui.accountChange

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.ui.base.BaseContract

interface ChangeAccountContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setAccounts(canShow : Boolean, sessions: List<UserSessionModel>)

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

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        fun logoutFromAccount(session: UserSessionModel)
        fun switchAccount(session: UserSessionModel)
        fun authToAccountClick()
        fun loginToAccountClick(user : UserDetail)
    }
}