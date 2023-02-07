package com.example.ui.notification.center.redesign

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.MyEventsFilter
import com.example.data.models.Notification
import com.example.databinding.FragmentNotificationsBinding
import com.example.databinding.FragmentNotificationsListBinding
import com.example.databinding.FragmentNotificationsListBindingImpl
import com.example.extensions.findItemBy
import com.example.holders.*
import com.example.interfaces.ToolbarFragmentNew
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.notification.center.NotificationsContract
import com.example.ui.notification.center.NotificationsFragmentDirections
import com.example.ui.notification.center.NotificationsPresenter
import com.example.ui.notification.center.redesign.items.AcceptNotificationItemNew
import com.example.ui.notification.center.redesign.items.NotificationsItemsGroup
import com.example.ui.notification.center.redesign.items.RateNotificationItemNew
import com.example.ui.notification.center.redesign.items.SimpleNotificationItemNew
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
import kotlin.math.abs

class NotificationsListFragment : BaseFragmentNew<FragmentNotificationsListBinding>(),
    NotificationsListContract.View {


    @InjectPresenter
    lateinit var presenter: NotificationsListPresenter

    @Inject
    lateinit var presenterProvider: Provider<NotificationsListPresenter>

    @ProvidePresenter
    fun providePresenter(): NotificationsListPresenter = presenterProvider.get()

    private val readClickListener: OnNotificationReadClickListener =
        { presenter.onNotificationReadClick(it) }

    private val acceptClickListener: OnNotificationAcceptClickListener = { notification, isAccept ->
        presenter.apply {
            if (isAccept) onNotificationAcceptClick(notification)
            else onNotificationCancelClick(notification)
        }
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

    private val groupAdapter by lazy {
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
        mBinding.apply {
            notificationsList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = groupAdapter
                onScrolled { _, _ -> }
            }
            btnReadAll.setOnClickListener {
                presenter.onReadAllNotificationsClick()
            }
            btnShowNotRead.setOnCheckedChangeListener { _, isChecked ->
                presenter.showOnlyNotRead(isChecked)
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setNotReadButtonEnabled(enabled: Boolean) {
        mBinding.btnShowNotRead.apply {
            isEnabled = enabled
            alpha = if (enabled) 1.0f
            else 0.5f
        }
    }

    override fun setData(notifications: List<Notification?>) {
        groupAdapter.update(notifications.map {
            PlaceholderItem(PlaceholderItem.Type.NOTIFICATION)
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun setDataNew(notifications: Map<String, List<Notification>>) {
        mBinding.swipeToRefresh.isRefreshing = false
        groupAdapter.update(notifications.map {
            NotificationsItemsGroup(
                requireContext(),
                it.key,
                it.value,
                linkClickListener,
                readClickListener,
                openEventListener,
                acceptClickListener,
                rateClickListener
            )
        })
    }

    override fun showEmptyListPlaceholder() {
        groupAdapter.update(listOf(NoEventItem(getString(R.string.notifications_not_found))))
        mBinding.swipeToRefresh.isRefreshing = false
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
        groupAdapter.findItemBy { item: Item -> item.id == idLong }?.apply {
            notifyChanged()
        }
    }


    fun smoothScrollToFirstItem() {
        val mLayoutManager =
            mBinding.notificationsList.layoutManager as LinearLayoutManagerAccurateOffset
        mLayoutManager.smoothScrollToFirstItem(requireContext(), null, 3)
    }


    override fun layout() = R.layout.fragment_notifications_list
}