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
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.AboutEventFragmentNewArgs
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.notification.center.NotificationsFragmentDirections
import com.example.ui.notification.center.redesign.invites.InviteNotificationsBottomSheet
import com.example.ui.notification.center.redesign.items.NotificationItemNew
import com.example.ui.notification.center.redesign.items.NotificationsItemsGroup
import com.example.ui.notification.center.redesign.items.NotificationsTagsItem
import com.example.ui.views.LinearLayoutManagerAccurateOffset
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.showCustomTabsBrowser
import com.example.util.smoothScrollToFirstItem
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
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
        override fun onLinkClickListener(url: String) = presenter.onNotificationUrlClick(url)
        override fun onAcceptClickListener(notification: Notification, isAccept: Boolean) {
            presenter.apply {
                if (isAccept) onNotificationAcceptClick(notification)
                else onNotificationCancelClick(notification)
            }
        }
    }

    private val tagsSection = Section()
    private val notificationsSection = Section()

    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(tagsSection)
            add(notificationsSection)
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
            }
            btnReadAll.setOnClickListener {
                presenter.onReadAllNotificationsClick()
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setNotReadButtonEnabled(enabled: Boolean) {
        mBinding.btnReadAll.isVisible = enabled
    }

    override fun setPlaceholder(notifications: List<Notification?>) {
        notificationsSection.update(notifications.map {
            PlaceholderItem(PlaceholderItem.Type.NOTIFICATIONS_LIST)
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun setDataNew(notifications: Map<String, List<Notification>>) {
        mBinding.swipeToRefresh.isRefreshing = false
        if (tagsSection.itemCount == 0)
            tagsSection.updateItem(NotificationsTagsItem { showNotificationsType(it) })

        notificationsSection.update(notifications.map {
            NotificationsItemsGroup(
                requireContext(),
                it.key,
                it.value,
                onNotificationListener
            )
        })
    }

    override fun showEmptyListPlaceholder() {
        notificationsSection.updateItem(NoEventItem(getString(R.string.notifications_not_found)))
        mBinding.swipeToRefresh.isRefreshing = false
    }


    override fun showAboutEvent(eventId: String) {
        if (!eventId.isNullOrEmpty()){
            findNavController().navigate(
                R.id.about_event_fragment_new,
                AboutEventFragmentNewArgs.Builder(eventId).build().toBundle()
            )
        }
    }

    override fun showAboutOrganization(id: String?) {
        findNavController().navigate(
            R.id.organization_fragment_new,
            bundleOf("organizationId" to id)
        )
    }

    private fun showNotificationsType(type: NotificationType) {
        when(type){
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

    override fun onNotificationNeedUpdate(id: Int) {
        val idLong = id.toLong()
        notificationsSection.findItemBy { item: Item -> item.id == idLong }?.apply {
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

enum class NotificationType {
    SYSTEM, PROJECTS, EVENTS, ESTIMATES, ORGANIZER
}