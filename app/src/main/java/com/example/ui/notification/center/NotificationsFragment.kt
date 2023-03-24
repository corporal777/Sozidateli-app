package com.example.ui.notification.center

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.FragmentNotificationsBinding
import com.example.extensions.findItemBy
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.views.LinearLayoutManagerAccurateOffset
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.showCustomTabsBrowser
import com.example.util.smoothScrollToFirstItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onScrolled
import javax.inject.Inject
import javax.inject.Provider


class NotificationsFragment : BaseFragmentNew<FragmentNotificationsBinding>(), NotificationsContract.View, ToolbarFragment {


    @InjectPresenter
    lateinit var presenter: NotificationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<NotificationsPresenter>

    @ProvidePresenter
    fun providePresenter(): NotificationsPresenter = presenterProvider.get()

    private val readMoreClickListener: OnNotificationReadMoreClickListener =
        { presenter.onNotificationReadMoreClick(it) }

    private val readClickListener: OnNotificationReadClickListener =
        { presenter.onNotificationReadClick(it) }

    private val readListener: OnNotificationReadListener =
        { presenter.onNotificationReadClick(it) }


    private val acceptClickListener: OnNotificationAcceptClickListener = { notification, isAccept ->
        presenter.apply {
            if (isAccept) onNotificationAcceptClick(notification)
            else onNotificationCancelClick(notification)
        }
    }

    private val changeDecisionClickListener: OnNotificationChangeDecisionClickListener = {
        presenter.onNotificationChangeDecisionClick(it)
    }

    private val rateClickListener: OnNotificationRateClickListener =
        { presenter.onNotificationRateClick(it) }

    private val openEventListener: OnOpenEventListener = {
        showAboutEvent(it)
    }

    private val linkClickListener = BetterLinkMovementMethod.OnLinkClickListener { _, url ->
        if (url.contains("/organization/")) {
            findNavController().navigate(
                R.id.organization_fragment_new,
                bundleOf("organizationId" to Uri.parse(url).lastPathSegment)
            )
        } else {
            presenter.onNotificationUrlClick(url)
        }
        true
    }

    private val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    if (position > 0) presenter.onItemTake(position - 1)
                }
            })
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
//        mBinding.notificationsList.apply {
//            layoutManager = LinearLayoutManagerAccurateOffset(requireContext())
//            adapter = this@NotificationsFragment.adapter
//            onScrolled { _, _ ->
//                presenter.changeAppBarElevation(this.computeVerticalScrollOffset())
//            }
//        }
    }

    override fun setNotificationsList() {
        mBinding.notificationsList.apply {
            layoutManager = LinearLayoutManagerAccurateOffset(requireContext())
            adapter = this@NotificationsFragment.adapter
        }
    }

    override fun setData(notifications: List<Notification?>) {
        adapter.update(notifications.map {
            if (it == null) {
                PlaceholderItem(PlaceholderItem.Type.NOTIFICATION)
            } else when (it.type) {
                Notification.Type.SIMPLE -> SimpleNotificationItem(
                    it,
                    readMoreClickListener,
                    linkClickListener,
                    readClickListener,
                    openEventListener
                )
                Notification.Type.ACCEPTABLE -> AcceptNotificationItem(
                    it,
                    readMoreClickListener,
                    linkClickListener,
                    acceptClickListener,
                    changeDecisionClickListener,
                    openEventListener,
                    readListener,
                )
//                Notification.Type.ACCEPTABLE -> SimpleNotificationItem(
//                    it,
//                    readMoreClickListener,
//                    linkClickListener,
//                    readClickListener,
//                    openEventListener
//                )
                Notification.Type.RATE -> RateNotificationItem(
                    it,
                    readMoreClickListener,
                    linkClickListener,
                    rateClickListener,
                    openEventListener
                )

            }

        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showEmptyListPlaceholder() {
        adapter.update(listOf(NoEventItem(getString(R.string.notifications_not_found))))
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showNotification(notification: Notification) {
        findNavController().navigate(
            NotificationsFragmentDirections.notificationsCenterToNotification(
                notification
            )
        )
    }

    override fun showAboutEvent(eventId: String) {
        findNavController().navigate(
            NotificationsFragmentDirections.notificationToAboutEventFragment(
                eventId
            )
        )
    }

    override fun showUrl(url: String) = showCustomTabsBrowser(requireContext(), url)

    override fun onNotificationNeedUpdate(id: Int) {
        val idLong = id.toLong()
        adapter.findItemBy { item: Item -> item.id == idLong }?.apply {
            notifyChanged()
        }
    }

    override fun showRating(eventId: String) {
        findNavController().navigate(
            NotificationsFragmentDirections.notificationsCenterToEventRating(
                eventId
            )
        )
    }

    fun smoothScrollToFirstItem() {
        val mLayoutManager =
            mBinding.notificationsList.layoutManager as LinearLayoutManagerAccurateOffset
        mLayoutManager.smoothScrollToFirstItem(requireContext(), null, 3)
    }


    override fun layout() = R.layout.fragment_notifications
    override val title: CharSequence by lazy { getString(R.string.notifications_label) }
    override fun actionIconContainer(view: ViewGroup) {}

    override fun scrollValue(scroll: (value: Int) -> Unit) {
        mBinding.notificationsList.apply {
            scroll.invoke(this.computeVerticalScrollOffset())
            onScrolled { _, _ ->
                scroll.invoke(this.computeVerticalScrollOffset())
            }
        }
    }

    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
