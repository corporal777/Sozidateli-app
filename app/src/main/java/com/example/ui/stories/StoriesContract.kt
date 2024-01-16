package com.example.ui.stories

import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface StoriesContract {
    interface View : MvpView {
        @OneExecution
        fun showAuthorization()
    }

    interface Presenter : BaseContract.Presenter {
        fun onStoriesComplete()
    }
}
