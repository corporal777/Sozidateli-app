package com.example.ui.event.list.favorite

import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.extensions.findItemBy
import com.example.holders.EventFavoriteItem
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.ui.event.favorite.subevent.FavoriteSubeventFragmentArgs
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

    override fun setData(events: List<EventNew/*Event*/?>) {
        dataGroup.update(events.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.SEARCH_EVENT)
            else EventFavoriteItem(
                    it,
                    { presenter.onShowEventClick(it.id?.toString()?:"0") },
                    { presenter.onEventActionClick(it) },
                    { presenter.onEventSubeventsClick(it) }
            )
        })
        swipeToRefresh.isRefreshing = false
    }

    override fun updateEventFavorite(eventId: String, isFavorite: Boolean) {
        dataGroup.findItemBy<EventFavoriteItem> { it.event.id?.toString() == eventId }?.apply {
            notifyChanged(isFavorite)
        }
    }

    override fun showSubEvents(event: String, subEvents: List</*SubEvent*/EventActivityModel>) {
        val args = FavoriteSubeventFragmentArgs.Builder(event, subEvents.toTypedArray()).build().toBundle()
        findNavController().navigate(R.id.favorite_subevents_fragment, args/*bundleOf()*/)
    }

    override fun showEmptyListPlaceholder() {
        dataGroup.update(listOf(NoDataItem(
                getString(R.string.empty_list_placeholder_message)/*getString(R.string.blank_list_error)*/,
                getString(R.string.events_favorites_empty_list_description)
        )))
        swipeToRefresh.isRefreshing = false
    }
}
