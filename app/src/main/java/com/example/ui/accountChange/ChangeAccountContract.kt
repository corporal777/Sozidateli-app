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
        fun setAccounts(users: Map<UserSessionModel, UserDetail>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUnLoggedAccounts(users: Map<UserSessionModel, UserDetail>)

        @StateStrategyType(SkipStrategy::class)
        fun showMessage(message : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAuthorizationFragment()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLoginFragment(login : String)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        fun logoutFromAccount(session: UserSessionModel)
        fun switchAccount(session: UserSessionModel, userDetail: UserDetail)
        fun authToAccountClick()
        fun loginToAccountClick(user : UserDetail)
    }
}