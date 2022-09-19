package com.example.ui.stories

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.util.OneExecutionByTagStateStrategy

interface StoriesContract {
    interface View : BaseContract.View {


        @StateStrategyType(OneExecutionByTagStateStrategy::class)
        fun showAuthorization()
    }

    interface Presenter : BaseContract.Presenter {
        fun onStoriesComplete()
    }
}
