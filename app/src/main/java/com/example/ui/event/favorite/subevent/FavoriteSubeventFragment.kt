package com.example.ui.event.favorite.subevent

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.holders.DayHeaderItem
import com.example.holders.NoDataItem
import com.example.ui.subevent.items.SubEventItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_event_contacts.recyclerView
import kotlinx.android.synthetic.main.layout_list.*
import javax.inject.Inject
import javax.inject.Provider

class FavoriteSubeventFragment : BaseFragment(), FavoriteSubeventContract.View, ToolbarFragment {

    override val title: String? = null

    @InjectPresenter
    lateinit var presenter: FavoriteSubeventPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteSubeventPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteSubeventPresenter = presenterProvider.get().apply {
        FavoriteSubeventFragmentArgs.fromBundle(requireArguments()).let {
            event = it.event
            actions = it.actions.asList()
        }
    }

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val onSubEventClickListener = object : SubEventItem.OnSubEventClickListener {
        override fun onSubEventClick(subEvent: EventActivityModel) {
            presenter.onSubEventClick(subEvent)
        }

        override fun onAddToScheduleClick(subEvent: EventActivityModel) {
            // do nothing
        }

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {
            // do nothing
        }

        override fun onChangeFavoriteClick(subEvent: EventActivityModel) {
            presenter.onChangeFavoriteRequest(subEvent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter
        }

        swipeToRefresh.setOnRefreshListener {
            presenter.onRefreshRequest()
        }
    }

    override fun setData(data: Map<Long?, List<EventActivityModel>>) {
        val groups = mutableListOf<Group>()
        data.forEach { entry ->
            val date = entry.key
            val events = entry.value
            if (date != null) {
                groups.add(DayHeaderItem(date))
                events.forEach {
                    groups.add(SubEventItem(it, SubEventItem.Mode.FAVORITE, onSubEventClickListener))
                }
            }
        }

        if (groups.isEmpty()) {
            groupAdapter.update(listOf(NoDataItem(getString(R.string.empty_list_placeholder_message))))
        } else {
            groupAdapter.update(groups)
        }

        swipeToRefresh.isRefreshing = false
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        findNavController().navigate(FavoriteSubeventFragmentDirections.favoriteSubeventsFragmentToSubeventFragment(eventId, subEventId))
    }

    override fun layout() = R.layout.fragment_event_speakers
}
