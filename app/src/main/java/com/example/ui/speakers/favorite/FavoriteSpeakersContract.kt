package com.example.ui.speakers.favorite

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Speaker
import com.example.ui.base.BaseContract

interface FavoriteSpeakersContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(data: PagedList<Speaker>)

        @StateStrategyType(SkipStrategy::class)
        fun showSpeaker(speaker: Speaker)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSpeakerClick(speaker: Speaker)
        fun onSpeakerFavoriteChangeClick(speaker: Speaker)
    }
}
