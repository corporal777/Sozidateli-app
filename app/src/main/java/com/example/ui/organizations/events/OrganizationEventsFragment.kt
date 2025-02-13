package com.example.ui.organizations.events

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.app.R
import com.example.app.databinding.LayoutListBinding
import com.example.data.models.EventNew
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventListItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.event.list.EventListFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class OrganizationEventsFragment : EventListFragment<OrganizationEventsPresenter, LayoutListBinding>(),
    OrganizationEventsContract.View, ToolbarFragment {


    @InjectPresenter
    override lateinit var presenter: OrganizationEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationEventsPresenter = presenterProvider.get().apply {
        organizationId = OrganizationEventsFragmentArgs.fromBundle(requireArguments()).organizationId
    }

    private val dataGroup = Section()
    private val groupAdapter = PaginationListGroupAdapter<GroupieViewHolder>().apply {
        add(dataGroup)
        setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
            override fun onItemTake(position: Int) {
                //if (position > 0) presenter.onItemTake(position - 1)
                presenter.onItemTake(position)
            }
        })
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
            if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
            else EventListItem(it, presenter.isTemporaryUser(), onEventClickListener)
        })

        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun updateEvent(event: EventNew) {
        val id = event.id?.toLong()
        dataGroup.findItemBy<EventListItem> { x -> x.id == id }?.notifyChanged(event)
    }

    override fun showEmptyListPlaceholder() {
        dataGroup.updateItem(NoDataItem(getString(R.string.empty_list_placeholder_message)))
        mBinding.swipeToRefresh.isRefreshing = false
    }


    override fun binding() = LayoutListBinding::class.java
    override fun layout(): Int = R.layout.layout_list
    override val title: CharSequence by lazy { getString(R.string.organization_events) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
