package com.example.ui.event.favorite.subevent

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.SubEvent
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.parseToDate
import com.example.extensions.startOfDay
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class FavoriteSubeventPresenter
@Inject constructor() : BasePresenter<FavoriteSubeventContract.View>(), FavoriteSubeventContract.Presenter {

    lateinit var actions: List<SubEvent>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val grouped = actions.filter {
            it.isInFavorites
        }.groupBy {
            it.start.parseToDate(defaultServerDateTimeFormatter)?.time?.startOfDay()
        }
        viewState.setData(grouped)
    }
}
