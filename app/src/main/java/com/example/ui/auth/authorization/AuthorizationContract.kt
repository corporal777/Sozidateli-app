package com.example.ui.auth.authorization

import android.content.Context
import com.example.ui.auth.base.BaseAuthContract
import com.example.data.models.SnAuth
import com.example.data.models.SnUser
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface AuthorizationContract {
    interface View : BaseAuthContract.View {
        @OneExecution
        fun setStories(stories: List<String>)

        @OneExecution
        fun showLogin()

        @OneExecution
        fun showRegistration()

        @OneExecution
        fun showSnAuthorization(snAuth: SnUser)

        @Skip
        fun showCustomLoading(type: Int)

        @Skip
        fun hideCustomLoading(type: Int)

        @Skip
        fun hideAllLoadings()
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onRegisterClick()
        fun onLoginClick()
        fun onAuthVkClick(context: Context)
    }
}
