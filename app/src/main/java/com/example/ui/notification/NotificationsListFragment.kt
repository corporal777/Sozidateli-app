package com.example.ui.notification

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.R
import com.example.data.models.Notification
import com.example.app.databinding.FragmentNotificationsListBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.notification.invites.InviteNotificationsBottomSheet
import com.example.ui.notification.items.*
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.showCustomTabsBrowser
import com.example.util.smoothScrollToFirstItem
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class NotificationsListFragment : BaseFragment<FragmentNotificationsListBinding>(),
    NotificationsListContract.View {


    @InjectPresenter
    lateinit var presenter: NotificationsListPresenter

    @Inject
    lateinit var presenterProvider: Provider<NotificationsListPresenter>

    @ProvidePresenter
    fun providePresenter(): NotificationsListPresenter = presenterProvider.get()


    private val onNotificationListener = object : NotificationItem.OnNotificationActionListener {
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
                    val tagsCount = tagsSection.itemCount
                    val datesCount = presenter.getTitleDatesCount()
                    if (position < (tagsCount + datesCount)) return
                    presenter.onItemTake(position - (tagsCount + datesCount))
                }
            })
        }
    }


    override fun onResume() {
        super.onResume()
        presenter.setFragmentOnResume(true)
    }

    override fun onPause() {
        super.onPause()
        presenter.setFragmentOnResume(false)
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
        mBinding.btnReadAll.isInvisible = !enabled
    }

    override fun setNotificationsPlaceholder() {
        notificationsSection.updateItem(PlaceholderItem(PlaceholderItem.Type.NOTIFICATIONS_MAIN))
        mBinding.swipeToRefresh.isRefreshing = false
    }


    override fun setData(notifications: List<NotificationsSortedData>) {
        mBinding.swipeToRefresh.isRefreshing = false
        if (tagsSection.itemCount == 0)
            tagsSection.updateItem(NotificationsTagsItem { showNotificationsType(it) })

        notificationsSection.update(notifications.map {
            Section().apply {
                if (!it.titleDate.isNullOrEmpty()) add(NotificationsDateItem(it.titleDate))
                add(
                    when (it.data.type) {
                        Notification.Type.SIMPLE -> SimpleNotificationItem(
                            requireContext(),
                            it.data,
                            onNotificationListener
                        )
                        Notification.Type.ACCEPTABLE -> AcceptNotificationItem(
                            requireContext(),
                            it.data,
                            onNotificationListener
                        )
                        Notification.Type.RATE -> RateNotificationItem(
                            requireContext(),
                            it.data,
                            onNotificationListener
                        )
                    }
                )
            }
        })
    }

    override fun showEmptyListPlaceholder() {
        tagsSection.update(emptyList())
        notificationsSection.updateItem(NoEventItem(getString(R.string.notifications_not_found)))
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun onNotificationNeedUpdate(data: Notification) {
        val date = data.date?.split(" ")?.get(0)
//        val group = notificationsSection.findGroupBy<NotificationsItemsGroup> { x -> x.isSameDate(date) }
//        if (group != null){
//            group.updateNotification(data)
//        }

        val idLong = data.id.toLong()
        val item = notificationsSection.findItemBy<NotificationItem<*>> { x -> x.id == idLong }
        if (item != null) item.notifyChanged(data)
    }


    override fun showAboutEvent(eventId: String) {
        if (!eventId.isNullOrEmpty()) {
            findNavController().navigate(
                R.id.about_event_fragment,
                AboutEventFragmentArgs.Builder(eventId).build().toBundle()
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

    override fun scrollToFirstItem() {
        val mLayoutManager =
            mBinding.notificationsList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), null, 3)
    }

    override fun layout() = R.layout.fragment_notifications_list
}

enum class NotificationType {
    SYSTEM, PROJECTS, EVENTS, ESTIMATES, ORGANIZER
}