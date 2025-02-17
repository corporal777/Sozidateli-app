package com.example.ui.notification.invites

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.R
import com.example.app.databinding.BottomSheetInviteNotificationsBinding
import com.example.data.models.Notification
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.notification.NotificationType
import com.example.ui.notification.NotificationsSortedData
import com.example.ui.notification.items.AcceptNotificationItem
import com.example.ui.notification.items.NotificationItem
import com.example.ui.notification.items.NotificationsDateItem
import com.example.ui.notification.items.RateNotificationItem
import com.example.ui.notification.items.SimpleNotificationItem
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.showCustomTabsBrowser
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class InviteNotificationsBottomSheet(val notificationType: NotificationType?) :
    BaseBottomSheetFragment<BottomSheetInviteNotificationsBinding>(),
    InviteNotificationsContract.View {

    @InjectPresenter(tag = INVITES_FRAGMENT_TAG)
    lateinit var presenter: InviteNotificationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<InviteNotificationsPresenter>

    @ProvidePresenter(tag = INVITES_FRAGMENT_TAG)
    fun providePresenter(): InviteNotificationsPresenter = presenterProvider.get().apply {
        type = notificationType
    }

    private val notificationsSection = Section()
    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(notificationsSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    val datesCount = presenter.getTitleDatesCount()
                    if (position < datesCount) return
                    presenter.onItemTake(position - datesCount)

                    //if (position > 0) presenter.onItemTake(position - 1)
                }
            })
        }
    }


    private val onNotificationListener = object : NotificationItem.OnNotificationActionListener {

        override fun onOpenEventClickListener(eventId: String) = showAboutEvent(eventId)
        override fun onReadClickListener(id: Int) {}
        override fun onRateClickListener(rateId: String) {}
        override fun onLinkClickListener(url: String) {
            if (url.contains("/organization/")) {
                showAboutOrganization(Uri.parse(url).lastPathSegment ?: "")
            } else presenter.onNotificationUrlClick(url)
        }


        override fun onAcceptClickListener(notification: Notification, isAccept: Boolean) {
            presenter.apply {
                if (isAccept) onNotificationAcceptClick(notification)
                else onNotificationCancelClick(notification)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            invitesList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = groupAdapter
            }
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun setUnreadInvitesLabel(invites: Int) {
        mBinding.apply {
            tvBottomSheetLabel.text = "Осталось приглашений: $invites"
            tvInviteInformation.apply {
                text = when (invites) {
                    1 -> "У вас осталось $invites приглашение, ожидающее вашего действия"
                    in 2..4 -> "У вас осталось $invites приглашения, ожидающих вашего действия"
                    else -> "У вас осталось $invites приглашений, ожидающих вашего действия"
                }
            }
        }
    }

    override fun setNotifications(notifications: List<NotificationsSortedData>) {
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


    override fun showUrl(url: String) = showCustomTabsBrowser(requireContext(), url)

    override fun showAboutEvent(eventId: String) {
        findNavController().navigate(
            R.id.about_event_fragment,
            AboutEventFragmentArgs.Builder(eventId).build().toBundle()
        )
    }

    override fun showAboutOrganization(organizationId: String) {
        findNavController().navigate(
            R.id.organization_fragment_new,
            OrganizationFragmentArgs.Builder(organizationId).build().toBundle()
        )
    }

    companion object {
        private const val INVITES_FRAGMENT_TAG = "invites_fragment"
    }

    override fun layout(): Int = R.layout.bottom_sheet_invite_notifications
}
