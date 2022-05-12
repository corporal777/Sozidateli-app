package com.example.ui.userprofile.academicdegree

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface EditDegreeContract {

    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "title")
        fun setTitle()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUserData(user: User)
    }

    interface Presenter : BaseContract.Presenter {

    }
}