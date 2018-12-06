package com.example.ui.eventSpeakers

import android.arch.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.User
import com.example.ui.base.BaseContract

interface SpeakersContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(data: PagedList<User>)

        @StateStrategyType(SkipStrategy::class)
        fun showSpeaker(user: User)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSpeakerClick(user: User)
    }
}
