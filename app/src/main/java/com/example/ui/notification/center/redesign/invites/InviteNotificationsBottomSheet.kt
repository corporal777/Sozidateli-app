package com.example.ui.notification.center.redesign.invites

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.BottomSheetInAppNotificationBinding
import com.example.databinding.BottomSheetInviteNotificationsBinding
import com.example.extensions.findItemBy
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.event.about.AboutEventFragmentNewArgs
import com.example.ui.main.inApp.InAppNotificationContract
import com.example.ui.main.inApp.InAppNotificationPresenter
import com.example.ui.notification.center.redesign.NotificationType
import com.example.ui.notification.center.redesign.items.*
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.showCustomTabsBrowser
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import javax.inject.Inject
import javax.inject.Provider
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class InviteNotificationsBottomSheet(val notificationType: NotificationType?) :
    BaseBottomSheetFragment<BottomSheetInviteNotificationsBinding>(),
    InviteNotificationsBottomSheetContract.View {

    @InjectPresenter(type = PresenterType.WEAK, tag = INVITES_FRAGMENT_TAG)
    lateinit var presenter: InviteNotificationsBottomSheetPresenter

    @Inject
    lateinit var presenterProvider: Provider<InviteNotificationsBottomSheetPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = INVITES_FRAGMENT_TAG)
    fun providePresenter(): InviteNotificationsBottomSheetPresenter =
        presenterProvider.get().apply {
            type = notificationType
        }

    private val notificationsSection = Section()
    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(notificationsSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    if (position > 0) presenter.onItemTake(position - 1)
                }
            })
        }
    }


    private val onNotificationListener = object : NotificationItemNew.OnNotificationActionListener {

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

    override fun setNotifications(notifications: Map<String, List<Notification>>) {
//        notificationsSection.update(notifications.map {
//            NotificationsItemsGroup(
//                requireContext(),
//                it.key,
//                it.value,
//                onNotificationListener
//            )
//        })
    }


    override fun showUrl(url: String) = showCustomTabsBrowser(requireContext(), url)

    override fun showAboutEvent(eventId: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(eventId).build().toBundle()
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
