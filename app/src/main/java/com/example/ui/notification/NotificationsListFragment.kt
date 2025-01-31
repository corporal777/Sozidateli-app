package com.example.ui.notification

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.EventPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.EventPlaceholderAdapter
import com.example.adapters.notification.NotificationPagerAdapter
import com.example.adapters.notification.NotificationPagerAdapter.Companion.withLoadStateAdapters
import com.example.adapters.notification.NotificationPlaceholderAdapter
import com.example.adapters.notification.NotificationTagsAdapter
import com.example.adapters.notification.NotificationVH
import com.example.app.R
import com.example.data.models.Notification
import com.example.app.databinding.FragmentNotificationsListBinding
import com.example.data.models.NotificationLocal
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.notification.invites.InviteNotificationsBottomSheet
import com.example.ui.notification.items.*
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.ui.views.loading.CustomCircleLoadingButton
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.showCustomTabsBrowser
import com.example.util.smoothScrollToFirstItem
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class NotificationsListFragment : BaseFragment<FragmentNotificationsListBinding>(),
    NotificationsListContract.View, ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: NotificationsListPresenter

    @Inject
    lateinit var presenterProvider: Provider<NotificationsListPresenter>

    @ProvidePresenter
    fun providePresenter(): NotificationsListPresenter = presenterProvider.get()


    private val onNotificationListener = object : NotificationVH.OnNotificationActionListener {
        override fun onReadClick(id: Int) = presenter.onNotificationReadClick(id)
        override fun onRateClick(rateId: String) = presenter.onNotificationRateClick(rateId)
        override fun onLinkClick(url: String) = presenter.onNotificationUrlClick(url)
        override fun onOpenEventClick(eventId: String) = showAboutEvent(eventId)
        override fun onAcceptClick(notification: NotificationLocal, isAccept: Boolean) =
            presenter.let {
                //if (isAccept) it.onNotificationAcceptClick(notification)
                //else it.onNotificationCancelClick(notification)
            }
    }

    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        NotificationPagerAdapter(onNotificationListener)
    }

    private val tagsAdapter by lazy(LazyThreadSafetyMode.NONE) {
        NotificationTagsAdapter { showNotificationsType(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            notificationsList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pagingAdapter.withLoadStateAdapters(
                    tagsAdapter,
                    NotificationPlaceholderAdapter(true),
                    NotificationPlaceholderAdapter(false)
                ) { tvEmptyData.isVisible = it }
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setNotReadButtonEnabled(enabled: Boolean) {
        btnReadAll.isVisible = enabled
    }

    override fun setData(notifications: PagingData<NotificationLocal>) {
        mBinding.swipeToRefresh.isRefreshing = false
        pagingAdapter.submitData(lifecycle, notifications)
    }

    override fun updateNotification(data: NotificationLocal?, notificationId: Int) {
        if (data == null) pagingAdapter.updateUserNotificationWithoutChange(notificationId)
        else pagingAdapter.updateUserNotification(data)
    }


    override fun showAboutEvent(eventId: String) {
        if (eventId.isEmpty()) return
        val args = AboutEventFragmentArgs.Builder(eventId).build().toBundle()
        findNavController().navigate(R.id.about_event_fragment, args)
    }

    override fun showAboutOrganization(id: String?) {
        if (id.isNullOrEmpty()) return
        val args = OrganizationFragmentArgs.Builder(id).build().toBundle()
        findNavController().navigate(R.id.organization_fragment_new, args)
    }

    private fun showNotificationsType(type: NotificationType) {
        when (type) {
            NotificationType.SYSTEM -> findNavController().navigate(R.id.system_notifications_fragment)
            NotificationType.PROJECTS -> findNavController().navigate(R.id.project_notifications_fragment)
            NotificationType.EVENTS -> findNavController().navigate(R.id.event_notifications_fragment)
            NotificationType.ORGANIZER -> findNavController().navigate(R.id.organizer_notifications_fragment)
            NotificationType.ESTIMATES -> findNavController().navigate(R.id.evaluate_notifications_fragment)
        }
    }

    override fun showInvitesBottomSheet() {
        val inviteNotifications = InviteNotificationsBottomSheet(null)
        inviteNotifications.show(requireActivity().supportFragmentManager, "invitesDialog")
    }

    override fun showUrl(url: String) = showCustomTabsBrowser(requireContext(), url)

    override fun showCustomLoading() = btnReadAll.showProgressLoading(true)
    override fun hideCustomLoading() = btnReadAll.showProgressLoading(false)

    override fun scrollToFirstItem() {
        val mLayoutManager =
            mBinding.notificationsList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), null, 3)
    }

    override fun layout() = R.layout.fragment_notifications_list
    override val title: CharSequence by lazy { getString(R.string.notifications_label) }
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().isInvisible = true
    }
    override fun actionIconContainer(view: ViewGroup) { view.addView(btnReadAll, 0) }

    private val btnReadAll by lazy {
        CustomCircleLoadingButton(requireContext()).apply {
            buttonText = requireContext().getString(R.string.read_all)
            isVisible = false
            setOnClickListener { presenter.onReadAllNotificationsClick() }
        }
    }
}

enum class NotificationType {
    SYSTEM, PROJECTS, EVENTS, ESTIMATES, ORGANIZER
}