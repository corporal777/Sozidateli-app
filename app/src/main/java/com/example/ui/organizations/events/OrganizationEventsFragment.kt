package com.example.ui.organizations.events

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventNew
import com.example.databinding.LayoutListBinding
import com.example.extensions.updateItem
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventItemNew
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.AboutEventFragmentNewArgs
import com.example.ui.event.list.EventListFragment
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.EventRegistrationProfileFieldsDialog
import com.example.ui.views.StateType
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class OrganizationEventsFragment :
    EventListFragment<OrganizationEventsPresenter, LayoutListBinding>(),
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
                if (position > 0) presenter.onItemTake(position - 1)
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
            else EventItemNew(
                it,
                it.id.toString(),
                it.state,
                it.status?.value,
                it.binds?.currentUserRegistration?.status?.value,
                it.backgroundColor?.value,
                it.image?.uri,
                it.binds?.eventRegistrationState,
                it.userAgreement?.uri,
                it.binds?.currentUserRegistration?.id.toString(),
                it.name,
                it.address?.getShortAddress(),
                it.holdingDate?.from,
                it.holdingDate?.to,
                onEventClickListener,
            )
        })

        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showEmptyListPlaceholder() {
        dataGroup.updateItem(NoDataItem(getString(R.string.empty_list_placeholder_message)))
        mBinding.swipeToRefresh.isRefreshing = false
    }


    override fun layout(): Int = R.layout.layout_list
    override val title: CharSequence by lazy { getString(R.string.organization_events) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
