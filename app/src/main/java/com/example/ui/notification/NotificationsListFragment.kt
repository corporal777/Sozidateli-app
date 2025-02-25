package com.example.ui.notification

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.notification.NotificationPagerAdapter
import com.example.adapters.notification.NotificationPagerAdapter.Companion.withLoadStateAdapters
import com.example.adapters.notification.NotificationPlaceholderAdapter
import com.example.adapters.notification.NotificationTagsAdapter
import com.example.adapters.notification.NotificationVH
import com.example.app.R
import com.example.app.databinding.FragmentNotificationsListBinding
import com.example.data.models.NotificationLocal
import com.example.extensions.isVisibleAnim
import com.example.extensions.setArgument
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.notification.invites.InviteNotificationsBottomSheet
import com.example.ui.notification.invites.InviteNotificationsBottomSheet.Companion.INVITES_FRAGMENT_TAG
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.smoothScrollToFirstItem
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class NotificationsListFragment : BaseToolbarFragment<FragmentNotificationsListBinding>(),
    NotificationsListContract.View {

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
                if (isAccept) it.onNotificationAcceptClick(notification)
                else it.onNotificationCancelClick(notification)
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
                ) { setEmptyDataPlaceholder(it) }
                setEmptyDataPlaceholder(isEmptyData)
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setData(notifications: PagingData<NotificationLocal>) {
        mBinding.swipeToRefresh.isRefreshing = false
        pagingAdapter.submitData(lifecycle, notifications)
    }

    override fun updateNotification(data: NotificationLocal?, notificationId: Int) {
        if (data == null) pagingAdapter.updateNotificationWithoutChange(notificationId)
        else pagingAdapter.updateNotification(data)
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
        InviteNotificationsBottomSheet()
            .setArgument<InviteNotificationsBottomSheet>(INVITES_FRAGMENT_TAG, null)
            .show(requireActivity().supportFragmentManager)
    }

    override fun setEmptyDataPlaceholder(show: Boolean) {
        super.setEmptyDataPlaceholder(show)
        mBinding.tvEmptyData.isVisibleAnim = show
    }

    override fun setBtnReadAllEnabled(enabled: Boolean) = btnReadAll.let { it.isVisible = enabled }
    override fun showCustomLoading() = btnReadAll.showProgressLoading(true)
    override fun hideCustomLoading() = btnReadAll.showProgressLoading(false)

    override fun scrollToFirstItem() {
        val layoutManager = mBinding.notificationsList.layoutManager as LinearLayoutManager
        layoutManager.smoothScrollToFirstItem(requireContext(), null, 3)
    }


    override fun binding() = FragmentNotificationsListBinding::class.java
    override fun layout() = R.layout.fragment_notifications_list
    override val title: CharSequence by lazy { getString(R.string.notifications_label) }
    override fun actionIconContainer(view: ViewGroup) { view.addView(btnReadAll, 0) }
    override fun scrollingView(): View = mBinding.notificationsList
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().isInvisible = true
    }
    private val btnReadAll by lazy { createButtonView(R.string.read_all, false) { presenter.onReadAllClick() } }
}

enum class NotificationType {
    SYSTEM, PROJECTS, EVENTS, ESTIMATES, ORGANIZER
}