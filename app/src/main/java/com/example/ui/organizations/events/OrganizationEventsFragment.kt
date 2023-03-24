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
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventItemNew
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.AboutEventFragmentNewArgs
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

class OrganizationEventsFragment : BaseFragmentNew<LayoutListBinding>(), OrganizationEventsContract.View, ToolbarFragment {


    @InjectPresenter
    lateinit var presenter: OrganizationEventsPresenter

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

    private val onEventClickListener = object : EventItemNew.OnEventClickListener {
        override fun onActionRegister(event: String) = presenter.onActionRegister(event)
        override fun onActionCancel(event: String, registrationId: String?) = presenter.onActionCancel(event, registrationId)
        override fun onShowEventClick(view: View, event: String) = presenter.onShowEventClick(event)
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
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
        dataGroup.update(listOf(NoDataItem(getString(R.string.empty_list_placeholder_message))))
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(event).build().toBundle())

    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(
            R.id.request_fragment,
            EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        )
    }


    override fun showRegistrationFieldsRequest(fields: List<String>) {
        EventRegistrationProfileFieldsDialog(requireContext(), fields) {
            presenter.onShowEditProfileClick()
        }.show()
    }

    override fun showEditProfile(id: String) {
        findNavController().navigate(
            R.id.user_profile_fragment,
            UserFragmentArgs.Builder(id).build().toBundle()
        )
    }

    override fun layout(): Int = R.layout.layout_list
    override val title: CharSequence by lazy { getString(R.string.organization_events) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {
        mBinding.recyclerView.apply {
            scroll.invoke(this.computeVerticalScrollOffset())
            onScrolled { _, _ -> scroll.invoke(this.computeVerticalScrollOffset()) }
        }
    }
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
