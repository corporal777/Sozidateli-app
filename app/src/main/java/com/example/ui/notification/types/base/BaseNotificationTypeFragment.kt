package com.example.ui.notification.types.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.notification.NotificationPagerAdapter
import com.example.adapters.notification.NotificationPagerAdapter.Companion.withLoadStateAdapters
import com.example.adapters.notification.NotificationPlaceholderAdapter
import com.example.adapters.notification.NotificationTagsAdapter
import com.example.app.R
import com.example.app.databinding.FragmentNotificationsTypesBinding
import com.example.data.models.NotificationLocal
import com.example.extensions.isVisibleAnim
import com.example.extensions.onScrolled
import com.example.extensions.setArgument
import com.example.ui.base.BaseVBFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.notification.NotificationType
import com.example.ui.notification.invites.InviteNotificationsBottomSheet
import com.example.ui.notification.invites.InviteNotificationsBottomSheet.Companion.INVITES_FRAGMENT_TAG


abstract class BaseNotificationTypeFragment<P : BaseNotificationTypeContract.Presenter> :
    BaseVBFragment<FragmentNotificationsTypesBinding>(), BaseNotificationTypeContract.View {

    abstract var presenter: P

    abstract val pagingAdapter: NotificationPagerAdapter

    open var tagsAdapter: NotificationTagsAdapter? = null

    @CallSuper
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
                onScrolled { _, _ ->
                    presenter.changeScrollingElevation(this.computeVerticalScrollOffset())
                }
            }
            toolbarLabel.text = title
            btnReadAll.setOnClickListener { presenter.onReadAllNotifications(type) }
            ivBack.setOnClickListener { navigateUp() }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun updateNotification(notificationId: Int) {
        pagingAdapter.updateNotificationWithoutChange(notificationId)
    }

    override fun updateNotification(data: NotificationLocal) {
        pagingAdapter.updateNotification(data)
    }


    override fun setEmptyDataPlaceholder(show: Boolean) {
        super.setEmptyDataPlaceholder(show)
        mBinding.tvEmptyData.isVisibleAnim = show
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun setAppBarElevation(shadow: Float) {
        mBinding.cvAppBar.cardElevation = if (shadow <= 10f) shadow else 10f
    }

    override fun setReadAllButton(show: Boolean) {
        mBinding.apply {
            btnReadAll.isVisible = show
            if (btnReadAll.isVisible) btnLeft.isInvisible = true else btnLeft.isVisible = false
            toolbarLabel.isVisible = true
        }
    }

    override fun showAboutEvent(eventId: String?) {
        if (eventId.isNullOrEmpty()) return
        val args = AboutEventFragmentArgs.Builder(eventId).build().toBundle()
        findNavController().navigate(R.id.about_event_fragment, args)
    }

    override fun showAboutOrganization(organizationId: String?) {
        if (organizationId.isNullOrEmpty()) return
        val args = bundleOf("organizationId" to organizationId)
        findNavController().navigate(R.id.organization_fragment_new, args)
    }

    override fun showInvitesBottomSheet(type: NotificationType) {
        InviteNotificationsBottomSheet()
            .setArgument<InviteNotificationsBottomSheet>(INVITES_FRAGMENT_TAG, type)
            .show(requireActivity().supportFragmentManager)
    }


    abstract val title: CharSequence
    abstract val type: NotificationType

    override fun binding() = FragmentNotificationsTypesBinding::class.java
    override fun layout(): Int = R.layout.fragment_notifications_types
}