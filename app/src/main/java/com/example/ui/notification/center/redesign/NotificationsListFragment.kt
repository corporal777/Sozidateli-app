package com.example.ui.notification.center.redesign

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.FragmentNotificationsListBinding
import com.example.extensions.findItemBy
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.notification.center.NotificationsFragmentDirections
import com.example.ui.notification.center.redesign.items.NotificationItemNew
import com.example.ui.notification.center.redesign.items.NotificationsItemsGroup
import com.example.ui.views.LinearLayoutManagerAccurateOffset
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.showCustomTabsBrowser
import com.example.util.smoothScrollToFirstItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class NotificationsListFragment : BaseFragmentNew<FragmentNotificationsListBinding>(),
    NotificationsListContract.View {


    @InjectPresenter
    lateinit var presenter: NotificationsListPresenter

    @Inject
    lateinit var presenterProvider: Provider<NotificationsListPresenter>

    @ProvidePresenter
    fun providePresenter(): NotificationsListPresenter = presenterProvider.get()


    private val onNotificationListener = object : NotificationItemNew.OnNotificationActionListener {

        override fun onOpenEventClickListener(eventId: String) = showAboutEvent(eventId)
        override fun onReadClickListener(id: Int) = presenter.onNotificationReadClick(id)
        override fun onRateClickListener(rateId: String) = presenter.onNotificationRateClick(rateId)

        override fun onReadListener(id: Int) {
            if (mBinding.notificationsList.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                //presenter.onNotificationRead(id)
            }
        }

        override fun onLinkClickListener(url: String) {
            if (url.contains("/organization/")) {
                showAboutOrganization(Uri.parse(url).lastPathSegment)
            } else presenter.onNotificationUrlClick(url)
        }


        override fun onAcceptClickListener(notification: Notification, isAccept: Boolean) {
            presenter.apply {
                if (isAccept) onNotificationAcceptClick(notification)
                else onNotificationCancelClick(notification)
            }
        }

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
        mBinding.btnReadAll.isVisible = enabled
    }

    override fun setData(notifications: List<Notification?>) {
        groupAdapter.update(notifications.map {
            PlaceholderItem(PlaceholderItem.Type.NOTIFICATIONS_LIST)
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
                onNotificationListener
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

    private fun showAboutOrganization(id: String?) {
        findNavController().navigate(
            R.id.organization_fragment_new,
            bundleOf("organizationId" to id)
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


    private fun getFirstVisibleItem(): Int {
        var position = 0
        mBinding.notificationsList.apply {
            val layoutManager = this.layoutManager as LinearLayoutManager
            position = layoutManager.findFirstCompletelyVisibleItemPosition()
        }
        return position
    }

    private fun getLastVisibleItem(): Int {
        var position = 0
        mBinding.notificationsList.apply {
            val layoutManager = this.layoutManager as LinearLayoutManager
            position = layoutManager.findLastCompletelyVisibleItemPosition()
        }
        return position + 1
    }


    override fun layout() = R.layout.fragment_notifications_list
}