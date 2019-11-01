package com.example.ui.stories

import com.arellomobile.mvp.MvpView
import com.example.ui.base.BaseContract

interface StoriesContract {
    interface View : MvpView

    interface Presenter : BaseContract.Presenter {
        fun onStoriesComplete()
    }
}
