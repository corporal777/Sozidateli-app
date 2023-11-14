package com.example.ui.main.inApp

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.BottomSheetInAppNotificationBinding
import com.example.extensions.findItemBy
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.notification.items.AcceptNotificationItem
import com.example.ui.notification.items.NotificationItem
import com.example.ui.notification.items.RateNotificationItem
import com.example.ui.notification.items.SimpleNotificationItem
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.util.showCustomTabsBrowser
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class InAppNotificationFragment(private val inAppList: List<Notification>) :
    BaseBottomSheetFragment<BottomSheetInAppNotificationBinding>(),
    InAppNotificationContract.View {

    @InjectPresenter(tag = IN_APP_FRAGMENT_TAG)
    lateinit var presenter: InAppNotificationPresenter

    @Inject
    lateinit var presenterProvider: Provider<InAppNotificationPresenter>

    @ProvidePresenter(tag = IN_APP_FRAGMENT_TAG)
    fun providePresenter(): InAppNotificationPresenter = presenterProvider.get().apply {
        this.notificationsList.addAll(inAppList)
    }

    private val notificationsSection = Section()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(notificationsSection)
    }


    private val onNotificationListener = object : NotificationItem.OnNotificationActionListener {

        override fun onOpenEventClickListener(eventId: String) = showAboutEvent(eventId)
        override fun onReadClickListener(id: Int) = presenter.onNotificationReadClick(id)
        override fun onRateClickListener(rateId: String) = presenter.onNotificationRateClick(rateId)

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
            inAppList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = groupAdapter
            }
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }


    override fun setNotifications(notifications: List<Notification>) {
        notificationsSection.update(notifications.map {
            when (it.type) {
                Notification.Type.SIMPLE -> SimpleNotificationItem(
                    requireContext(),
                    it,
                    onNotificationListener
                )
                Notification.Type.ACCEPTABLE -> AcceptNotificationItem(
                    requireContext(),
                    it,
                    onNotificationListener
                )
                Notification.Type.RATE -> RateNotificationItem(
                    requireContext(),
                    it,
                    onNotificationListener
                )
            }
        })
    }


    override fun onNotificationNeedUpdate(notification: Notification) {
        val idLong = id.toLong()
        groupAdapter.findItemBy { item: SimpleNotificationItem -> item.id == idLong }?.apply {
            notifyChanged(notification)
        }
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
        private const val IN_APP_FRAGMENT_TAG = "in_app_fragment"
    }

    override fun layout(): Int = R.layout.bottom_sheet_in_app_notification
}