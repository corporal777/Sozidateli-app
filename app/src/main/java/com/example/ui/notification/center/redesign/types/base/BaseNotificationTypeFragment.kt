package com.example.ui.notification.center.redesign.types.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.Notification
import com.example.data.models.Organization
import com.example.databinding.FragmentMaxStateInfoBinding
import com.example.databinding.LayoutListBinding
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.AboutEventFragmentNewArgs
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.main.inApp.InAppNotificationFragment
import com.example.ui.notification.center.NotificationsFragmentDirections
import com.example.ui.notification.center.redesign.NotificationType
import com.example.ui.notification.center.redesign.invites.InviteNotificationsBottomSheet
import com.example.ui.state.maxNew.base.BaseMaxStateContract
import com.example.ui.views.toolbar.ToolbarCircleButton
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.showCustomTabsBrowser
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

abstract class BaseNotificationTypeFragment<P : BaseNotificationTypeContract.Presenter> :
    BaseFragmentNew<LayoutListBinding>(),
    BaseNotificationTypeContract.View, ToolbarFragment {

    abstract var presenter: P

    private val readAllButton by lazy {
        ToolbarCircleButton(requireContext()).apply {
            text = requireContext().getString(R.string.read_all)
            isVisible = false
            setOnClickListener { presenter.onReadAllClick() }
        }
    }

    val contentSection by lazy { Section() }
    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(contentSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    if (position > 0) presenter.onItemTake(position - 1)
                }
            })
        }
    }


    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.adapter = groupAdapter
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setPlaceholder(notifications: List<Notification?>) {
        contentSection.update(notifications.map {
            PlaceholderItem(PlaceholderItem.Type.NOTIFICATIONS_LIST)
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showEmptyListPlaceholder() {
        contentSection.updateItem(NoEventItem(getString(R.string.notifications_not_found)))
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showAboutEvent(eventId: String?) {
        if (!eventId.isNullOrEmpty()){
            findNavController().navigate(
                R.id.about_event_fragment_new,
                AboutEventFragmentNewArgs.Builder(eventId).build().toBundle()
            )
        }
    }

    override fun showAboutOrganization(organizationId: String?) {
        if (!organizationId.isNullOrEmpty()){
            findNavController().navigate(
                R.id.organization_fragment_new,
                bundleOf("organizationId" to organizationId)
            )
        }
    }

    override fun showInvitesBottomSheet(type: NotificationType) {
        val inviteNotifications = InviteNotificationsBottomSheet(type)
        inviteNotifications.show(requireActivity().supportFragmentManager, "invitesDialog")
    }

    override fun showUrl(url: String) = showCustomTabsBrowser(requireContext(), url)

    override fun setReadAllButton(show: Boolean) {
        readAllButton.isVisible = show
    }

    override fun layout(): Int = R.layout.layout_list
    override fun actionIconContainer(view: ViewGroup) {
        view.apply {
            removeAllViews()
            addView(readAllButton)
        }
    }

    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
}