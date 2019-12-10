package com.example.ui.event.list.favorite

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.data.models.Event
import com.example.holders.EventFavoriteItem
import com.example.holders.PlaceholderItem
import com.example.ui.event.list.EventListFragment
import kotlinx.android.synthetic.main.layout_list.*
import javax.inject.Inject
import javax.inject.Provider

class FavoriteEventsFragment : EventListFragment<FavoriteEventsPresenter>(), FavoriteEventsContract.View {

    @InjectPresenter
    override lateinit var presenter: FavoriteEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteEventsPresenter = presenterProvider.get()

    override fun setData(events: List<Event?>) {
        dataGroup.update(events.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.SEARCH_EVENT)
            else EventFavoriteItem(it) {}
        })
        swipeToRefresh.isRefreshing = false
    }
}
