package com.example.ui.stories

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface StoriesContract {
    interface View : BaseContract.View {
        @OneExecution
        fun showAuthorization()
    }

    interface Presenter : BaseContract.Presenter {
        fun onStoriesComplete()
    }
}
