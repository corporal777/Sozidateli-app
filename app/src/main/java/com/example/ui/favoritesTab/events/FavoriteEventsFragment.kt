package com.example.ui.favoritesTab.events

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.databinding.LayoutListBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.EventFavoriteItem
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentNewArgs
import com.example.ui.event.favorite.subevent.FavoriteSubeventFragmentArgs
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

class FavoriteEventsFragment : BaseFragment<LayoutListBinding>(), FavoriteEventsContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteEventsPresenter = presenterProvider.get()

    private val dataGroup = Section()
    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(dataGroup)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(position)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.apply {
                adapter = groupAdapter
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setData(events: List<EventNew?>) {
        dataGroup.update(events.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.SEARCH_EVENT)
            else EventFavoriteItem(
                it,
                { presenter.onShowEventClick(it.id?.toString()) },
                { presenter.onEventActionClick(it) },
                { presenter.onEventSubEventsClick(it) }
            )
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun updateEventFavorite(eventId: String, isFavorite: Boolean) {
        dataGroup.findItemBy<EventFavoriteItem> { it.event.id?.toString() == eventId }?.apply {
            notifyChanged(isFavorite)
        }
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(event).build().toBundle()
        )
    }

    override fun showSubEvents(event: String, subEvents: List<EventActivityModel>) {
        val args =
            FavoriteSubeventFragmentArgs.Builder(event, subEvents.toTypedArray()).build().toBundle()
        findNavController().navigate(R.id.favorite_subevents_fragment, args)
    }

    override fun showEmptyListPlaceholder() {
        dataGroup.updateItem(
            NoEventItem(
                getString(R.string.empty_list_placeholder_message),
                getString(R.string.events_favorites_empty_list_description)
            )
        )
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun layout(): Int = R.layout.layout_list
}
