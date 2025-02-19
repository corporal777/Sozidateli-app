package com.example.ui.notification.invites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.notification.NotificationPagerAdapter
import com.example.adapters.notification.NotificationPagerAdapter.Companion.withLoadStateAdapters
import com.example.adapters.notification.NotificationPlaceholderAdapter
import com.example.adapters.notification.NotificationVH
import com.example.app.R
import com.example.app.databinding.BottomSheetInviteNotificationsBinding
import com.example.data.models.Argument
import com.example.data.models.NotificationLocal
import com.example.extensions.parcelableArgument
import com.example.ui.base.bottomSheet.BaseBSFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.notification.NotificationType
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.util.showCustomTabsBrowser
import dev.androidbroadcast.vbpd.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider


class InviteNotificationsBottomSheet : BaseBSFragment(), InviteNotificationsContract.View {

    private val mBinding by viewBinding(BottomSheetInviteNotificationsBinding::bind)
    private val args by parcelableArgument<Argument<NotificationType>>(INVITES_FRAGMENT_TAG)


    @InjectPresenter(tag = INVITES_FRAGMENT_TAG)
    lateinit var presenter: InviteNotificationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<InviteNotificationsPresenter>

    @ProvidePresenter(tag = INVITES_FRAGMENT_TAG)
    fun providePresenter(): InviteNotificationsPresenter = presenterProvider.get().apply {
        type = args.value
    }

    private val onNotificationListener = object : NotificationVH.OnNotificationActionListener {
        override fun onReadClick(id: Int) {}
        override fun onRateClick(rateId: String) {}
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            invitesList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pagingAdapter.withLoadStateAdapters(
                    null,
                    NotificationPlaceholderAdapter(true),
                    NotificationPlaceholderAdapter(false)
                ) {  }
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

    override fun setNotifications(notifications: PagingData<NotificationLocal>) {
        pagingAdapter.submitData(lifecycle, notifications)
    }

    override fun updateNotification(data: NotificationLocal?, notificationId: Int) {
        if (data == null) pagingAdapter.updateUserNotificationWithoutChange(notificationId)
        else pagingAdapter.updateUserNotification(data)
    }



    override fun showAboutEvent(eventId: String) {
        val args = AboutEventFragmentArgs.Builder(eventId).build().toBundle()
        findNavController().navigate(R.id.about_event_fragment, args)
    }

    override fun showAboutOrganization(organizationId: String) {
        val args = OrganizationFragmentArgs.Builder(organizationId).build().toBundle()
        findNavController().navigate(R.id.organization_fragment_new, args)
    }

    override fun showUrl(url: String) = showCustomTabsBrowser(requireContext(), url)

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, INVITES_FRAGMENT_TAG)

    companion object {
        const val INVITES_FRAGMENT_TAG = "invites_fragment"
    }

    override fun layout(): Int = R.layout.bottom_sheet_invite_notifications
}
