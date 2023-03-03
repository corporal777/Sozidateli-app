package com.example.ui.main.inApp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.BottomSheetInAppNotificationBinding
import com.example.extensions.defaultDateTimeFormatter
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.findItemBy
import com.example.extensions.parseAndFormat
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.notification.center.redesign.items.AcceptNotificationItemNew
import com.example.ui.notification.center.redesign.items.NotificationItemNew
import com.example.ui.notification.center.redesign.items.RateNotificationItemNew
import com.example.ui.notification.center.redesign.items.SimpleNotificationItemNew
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.ui.userprofile.read.settings.change_email.ChangeEmailFragment
import com.example.util.showCustomTabsBrowser
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider

class InAppNotificationFragment(private val inAppList: List<Notification>) :
    BaseBottomSheetFragment<BottomSheetInAppNotificationBinding>(),
    InAppNotificationContract.View {

    @InjectPresenter(type = PresenterType.WEAK, tag = IN_APP_FRAGMENT_TAG)
    lateinit var presenter: InAppNotificationPresenter

    @Inject
    lateinit var presenterProvider: Provider<InAppNotificationPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = IN_APP_FRAGMENT_TAG)
    fun providePresenter(): InAppNotificationPresenter = presenterProvider.get().apply {
        this.notificationsList.addAll(inAppList)
    }

    private val notificationsSection = Section()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(notificationsSection)
    }


    private val onNotificationListener = object : NotificationItemNew.OnNotificationActionListener {

        override fun onOpenEventClickListener(eventId: String) = showAboutEvent(eventId)
        override fun onReadClickListener(id: Int) = presenter.onNotificationReadClick(id)
        override fun onRateClickListener(rateId: String) = presenter.onNotificationRateClick(rateId)
        override fun onReadListener(id: Int){
            //presenter.onNotificationRead(id)
        }

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
                Notification.Type.SIMPLE -> SimpleNotificationItemNew(
                    requireContext(),
                    it,
                    onNotificationListener
                )
                Notification.Type.ACCEPTABLE -> AcceptNotificationItemNew(
                    requireContext(),
                    it,
                    onNotificationListener
                )
                Notification.Type.RATE -> RateNotificationItemNew(
                    requireContext(),
                    it,
                    onNotificationListener
                )
            }
        })
    }


    override fun onNotificationNeedUpdate(notification: Notification) {
        val idLong = id.toLong()
        groupAdapter.findItemBy { item: SimpleNotificationItemNew -> item.id == idLong }?.apply {
            notifyChanged(notification)
        }
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
        private const val IN_APP_FRAGMENT_TAG = "in_app_fragment"
    }

    override fun layout(): Int = R.layout.bottom_sheet_in_app_notification
}