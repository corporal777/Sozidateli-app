package com.example.ui.notification.center

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.extensions.findItemBy
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragment
import com.example.ui.notification.NotificationFragmentDirections
import com.example.ui.notification.NotificationFragmentDirections.notificationToAboutEventFragment
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.layout_list.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import javax.inject.Inject
import javax.inject.Provider


class NotificationsFragment : BaseFragment(), NotificationsContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = getString(R.string.notifications_label)

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
        findNavController().navigate(
            NotificationsFragmentDirections.notificationToAboutEventFragment(
                it,
                AboutEventFragment.ABOUT_FROM_OTHER
            )
        )
    }

    private val linkClickListener = BetterLinkMovementMethod.OnLinkClickListener { _, url ->
        if (url.contains("/organization/")) {
            findNavController().navigate(
                R.id.organization_fragment,
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
        recyclerView.apply {
            adapter = this@NotificationsFragment.adapter
        }

        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setData(notifications: List<Notification?>) {
        adapter.update(notifications.map {

            if (it == null) {
                PlaceholderItem(PlaceholderItem.Type.NOTIFICATION)
            }
            else when (it.type) {
                Notification.Type.SIMPLE -> SimpleNotificationItem(
                    it,
                    readMoreClickListener,
                    linkClickListener,
                    readClickListener,
                    openEventListener
                )
//                Notification.Type.ACCEPTABLE -> AcceptNotificationItem(
//                    it,
//                    readMoreClickListener,
//                    linkClickListener,
//                    acceptClickListener,
//                    changeDecisionClickListener,
//                    openEventListener,
//                    readListener,
//                )
                Notification.Type.ACCEPTABLE -> SimpleNotificationItem(
                    it,
                    readMoreClickListener,
                    linkClickListener,
                    readClickListener,
                    openEventListener
                )
                Notification.Type.RATE -> RateNotificationItem(
                    it,
                    readMoreClickListener,
                    linkClickListener,
                    rateClickListener,
                    openEventListener
                )

            }

        })
        swipeToRefresh.isRefreshing = false
    }

    override fun showNotification(notification: Notification) {
        findNavController().navigate(
            NotificationsFragmentDirections.notificationsCenterToNotification(
                notification
            )
        )
    }

    override fun showUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }

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


    override fun layout() = R.layout.layout_list
}
