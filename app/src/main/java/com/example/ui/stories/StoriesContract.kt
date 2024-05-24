package com.example.ui.stories

import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface StoriesContract {
    interface View : BaseContract.View {
        @Skip
        fun hideStories()
    }

    interface Presenter : BaseContract.Presenter {
        fun onStoriesComplete()
    }
}
