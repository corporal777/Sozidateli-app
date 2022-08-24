package com.example.ui.notification

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.InviteDetail
import com.example.data.models.Notification
import com.example.databinding.FragmentNotificationBinding
import com.example.extensions.defaultDateTimeFormatter
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.parseAndFormat
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.redesign.AboutEventFragmentNew.Companion.ABOUT_FROM_OTHER
import com.example.ui.views.CtpDialog
import com.example.ui.views.GetMaxStateDialog
import com.example.ui.views.toolbar.SimpleTitleToolbar
import kotlinx.android.synthetic.main.fragment_notification.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onScrolled
import removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider

class NotificationFragment : BaseFragmentNew<FragmentNotificationBinding>(),
    NotificationContract.View, SimpleTitleToolbar {

    private var isCanceled = false
    private var isAccepted = false

    @InjectPresenter
    lateinit var presenter: NotificationPresenter

    @Inject
    lateinit var presenterProvider: Provider<NotificationPresenter>

    @ProvidePresenter
    fun providePresenter(): NotificationPresenter = presenterProvider.get().apply {
        NotificationFragmentArgs.fromBundle(requireArguments()).let {
            notification = it.notification
        }
    }

    private val linkClickListener = BetterLinkMovementMethod.OnLinkClickListener { _, url ->
        presenter.onNotificationUrlClick(url)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarTitle(getString(R.string.notification_label))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.nestedScrollView.onScrolled { scrollY, oldScrollY, _, _ ->
            presenter.changeAppBarElevation(scrollY - oldScrollY)
        }
    }

    override fun setData(notification: Notification) {
        mBinding.apply {
            tvDate.apply {
                val parsedDate = notification.date.parseAndFormat(
                    defaultServerDateTimeFormatter,
                    defaultDateTimeFormatter
                )
                text = parsedDate
            }

            tvMessage.apply {
                text = notification.message?.parseAsHtml()
                BetterLinkMovementMethod.linkifyHtml(this)
                    .setOnLinkClickListener { _, url ->
                        if (url.contains("https") || url.contains("http")) {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            startActivity(i)
                        } else if (url.contains("organization")) {
                            val organizationId = url.replace("organization", "").replace("/", "")
                            findNavController().navigate(
                                NotificationFragmentDirections.notificationToOrganizationFragment(
                                    organizationId
                                )
                            )
                            Log.INFO
                        } else if (url.contains("event")) {
                            val eventId = url.replace("event", "").replace("/", "")
                            findNavController().navigate(
                                NotificationFragmentDirections.notificationToAboutEventFragment(
                                    eventId,
                                    ABOUT_FROM_OTHER
                                )
                            )
                        } else {
                            Log.INFO
                        }
                        true
                    }
            }
        }

        //        //var canAccept = false
        //var canChangeAccept = false

        val titleRes: Int
        var actionTextRes: Int? = null
        var canRate = false

        when (notification.type) {
            Notification.Type.SIMPLE -> titleRes = R.string.notifications_simple_title
            Notification.Type.ACCEPTABLE -> {
                titleRes = R.string.notifications_acceptable_title
                when (notification.acceptState) {
                    //when (InviteDetail.getInviteState(invite)) {
                    Notification.AcceptState.DISABLED -> {
                        mBinding.tvActionText.isVisible = true
                        actionTextRes = R.string.notifications_state_disabled
                    }
                    Notification.AcceptState.ACCEPTED -> {
                        isAccepted = true
                        mBinding.apply {
                            btnAccept.apply {
                                isVisible = true
                                isEnabled = false
                                text = getString(R.string.notifications_state_accepted)
                            }
                            btnCancel.apply {
                                isVisible = true
                                isEnabled = true
                            }
                        }
                    }
                    Notification.AcceptState.CANCELED -> {
                        isCanceled = true
                        mBinding.apply {
                            btnAccept.apply {
                                isVisible = true
                                isEnabled = true
                            }
                            btnCancel.apply {
                                isVisible = true
                                isEnabled = false
                                text = getString(R.string.notifications_state_cancelled)
                            }
                        }
                    }
                    else -> {
                        mBinding.apply {
                            tvActionText.isVisible = false
                            btnAccept.apply {
                                isVisible = true
                                isEnabled = true
                            }
                            btnCancel.apply {
                                isVisible = true
                                isEnabled = true
                            }
                        }

                    }
                }
            }
            Notification.Type.RATE -> {
                titleRes = R.string.notifications_rate_title
                mBinding.btnRate.isVisible = !notification.wasRead
            }
        }

        mBinding.tvTitle.apply {
            if (notification.eventId != 0 && notification.eventActivityId == 0) {
                text = context.resources.getString(
                    R.string.notification_event_title,
                    "<br><br><a href=" + notification.eventInfo?.link + " target=_blank>«" + notification.eventInfo?.name + "»</a>"
                ).parseAsHtml()
                BetterLinkMovementMethod.linkifyHtml(this)
                    .setOnLinkClickListener { _, url ->
                        val eventMass = url.split("event")
                        val eventId = eventMass.last().replace("/", "")
                        findNavController().navigate(
                            NotificationFragmentDirections.notificationToAboutEventFragment(
                                eventId,
                                ABOUT_FROM_OTHER
                            )
                        )
                        true
                    }
                removeUrlUnderline()
            } else {
                text = getString(titleRes)
            }
        }

        mBinding.tvActionText.apply {
            isVisible = actionTextRes != null
            text = actionTextRes?.let { getString(it) }
        }

        mBinding.btnRate.setOnClickListener { presenter.onNotificationRateClick() }

        mBinding.btnAccept.setOnClickListener {
            presenter.onNotificationAcceptClick()
        }

        mBinding.btnCancel.setOnClickListener {
            if (isAccepted)
                showCancelInfo()
            else {
                presenter.onNotificationCancelClick()
            }
        }

        /*btnChangeDecision.apply {
            isVisible = canChangeAccept
            setOnClickListener { presenter.onNotificationChangeDecisionClick() }
        }*/
    }

    private fun showCancelInfo() {
        CtpDialog(requireContext())
            .setSelectCallback {}
    }

    override fun showUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }

    override fun showErrorDialog(errors: List<String>, projectName: String) {
//        ProfileDialog(
//            requireContext(),
//            projectName,
//            errors
//        ).setSelectCallback { findNavController().navigate(R.id.user_profile_fragment) }

        mBinding.btnAccept.isEnabled = true
        GetMaxStateDialog(requireContext())
            .setSelectCallback { findNavController().navigate(R.id.userStateFragment) }
    }

    override fun showSuccessAccepted() {
        isAccepted = true
        mBinding.apply {
            btnAccept.apply {
                text = getString(R.string.notifications_state_accepted)
                isEnabled = false
            }
            btnCancel.apply {
                isEnabled = true
                text = getString(R.string.notifications_cancel)
            }
        }
    }

    override fun showSuccessCanceled() {
        isCanceled = true
        mBinding.apply {
            btnAccept.apply {
                text = getString(R.string.notifications_accept)
                isEnabled = true
            }
            btnCancel.apply {
                text = getString(R.string.notifications_state_cancelled)
                isEnabled = false
            }
        }
    }

    override fun showRating(eventId: String) {
        findNavController().navigate(
            NotificationFragmentDirections.notificationToEventRating(
                eventId
            )
        )
    }

    override fun layout() = R.layout.fragment_notification
}
