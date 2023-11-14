package com.example.ui.event.favorite.subevent

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.databinding.LayoutListBinding
import com.example.extensions.updateItem
import com.example.holders.DayHeaderItem
import com.example.holders.NoDataItem
import com.example.ui.base.BaseFragment
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.subevent.items.SubEventItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class FavoriteSubeventFragment : BaseFragment<LayoutListBinding>(), FavoriteSubeventContract.View {


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
        override fun onAddToScheduleClick(subEvent: EventActivityModel) {}
        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {}
        override fun onChangeFavoriteClick(subEvent: EventActivityModel) {
            presenter.onChangeFavoriteRequest(subEvent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.apply {
                adapter = groupAdapter
            }

            swipeToRefresh.setOnRefreshListener {
                presenter.onRefreshRequest()
            }
        }
    }

    override fun setData(data: Map<Long?, List<EventActivityModel>>) {
        if (data.isNullOrEmpty()) groupAdapter.updateItem(NoDataItem(getString(R.string.empty_list_placeholder_message)))
        else {
            groupAdapter.update(
                data.map { entry ->
                    val date = entry.key
                    val events = entry.value
                    Section().apply {
                        if (date != null) {
                            add(DayHeaderItem(date))
                            events.forEach {
                                add(SubEventItem(it, SubEventItem.Mode.FAVORITE, onSubEventClickListener))
                            }
                        }
                    }
                }
            )
        }
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subEvent_fragment, args)
    }

    override fun layout() = R.layout.layout_list
}
