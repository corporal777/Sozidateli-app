package com.example.ui.speakers.event

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Speaker
import com.example.ui.base.BaseContract
import com.example.ui.speakers.base.BaseSpeakersContract
import com.example.ui.speakers.base.BaseSpeakersFragment
import com.example.ui.speakers.base.BaseSpeakersPresenter

interface EventSpeakersContract {
    interface View : BaseSpeakersContract.View {

    }

    interface Presenter : BaseSpeakersContract.Presenter {

    }
}
