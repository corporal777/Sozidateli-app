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
        fun setAccounts(canShow : Boolean, users: Map<UserSessionModel, UserDetail>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUnLoggedAccounts(canShow : Boolean, users: Map<UserSessionModel, UserDetail>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setLoginToAnotherAccountButton()

        @StateStrategyType(SkipStrategy::class)
        fun showMessage(message : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAuthorizationFragment()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLoginFragment(login : String)

        @StateStrategyType(SkipStrategy::class)
        fun showProgressLoading()

        @StateStrategyType(SkipStrategy::class)
        fun hideProgressLoading()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun removeLoggedAccount(id : Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun removeUnLoggedAccount(id : Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateAccounts(state : Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun addUnLoggedAccount(state: Boolean, session : UserSessionModel, user : UserDetail)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {
        fun logoutFromAccount(session: UserSessionModel, userDetail: UserDetail)
        fun switchAccount(session: UserSessionModel, userDetail: UserDetail)
        fun authToAccountClick()
        fun loginToAccountClick(user : UserDetail)
    }
}