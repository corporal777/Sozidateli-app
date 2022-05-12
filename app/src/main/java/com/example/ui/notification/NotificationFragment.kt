package com.example.ui.notification

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.extensions.defaultDateTimeFormatter
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.parseAndFormat
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.redesign.AboutEventFragmentNew.Companion.ABOUT_FROM_OTHER
import com.example.ui.views.CtpDialog
import com.example.ui.views.GetMaxStateDialog
import kotlinx.android.synthetic.main.fragment_notification.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider

class NotificationFragment : BaseFragment(), NotificationContract.View, ToolbarFragment {

    override val title: CharSequence? = null
    var isCanceled = false

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

    override fun setData(notification: Notification) {
        Log.e("NOTE", notification.toString())

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

        val titleRes: Int
        var actionTextRes: Int? = null
        var canRate = false
        var isAccepted = false


        //        //var canAccept = false
        //var canChangeAccept = false

        when (notification.type) {
            Notification.Type.SIMPLE -> {
                titleRes = R.string.notifications_simple_title
                btnAccept.isEnabled = false
                btnCancel.isEnabled = false
                btnAccept.isVisible = false
                btnCancel.isVisible = false
            }
            Notification.Type.ACCEPTABLE -> {
                titleRes = R.string.notifications_acceptable_title
                when (notification.acceptState) {
                    Notification.AcceptState.DISABLED -> {
                        tvActionText.isVisible = true
                        actionTextRes = R.string.notifications_state_disabled
                        btnAccept.isVisible = false
                        btnCancel.isVisible = false
                    }
                    Notification.AcceptState.ACCEPTED -> {
                        btnAccept.enableOrDisableButton(false)
                        btnCancel.enableOrDisableButton(true)
                        /*btnAccept.isEnabled = false
                        btnCancel.isEnabled = true*/
                        isAccepted = true

                        //canChangeAccept = true
                        //actionTextRes = R.string.notifications_state_accepted
                        btnAccept.text = getString(R.string.notifications_state_accepted)
                    }
                    Notification.AcceptState.CANCELED -> {
                        btnAccept.enableOrDisableButton(true)
                        btnCancel.enableOrDisableButton(false)
                        isCanceled = true
                        /*btnAccept.isEnabled = true
                        btnAccept.setBackgroundResource(R.drawable.background_corners)
                        btnCancel.isEnabled = false
                        btnCancel.setBackgroundResource(R.drawable.background_corners_disabled)*/
                        //canChangeAccept = true
                        //actionTextRes = R.string.notifications_state_cancelled
                        btnCancel.text = getString(R.string.notifications_state_cancelled)

                    }
                    else -> {
                        tvActionText.isVisible = false

                        btnAccept.enableOrDisableButton(true)
                        btnCancel.enableOrDisableButton(true)
                        /*btnAccept.isEnabled = true
                        btnCancel.isEnabled = true*/
                        //canAccept = true
                    }
                }
            }
            Notification.Type.RATE -> {
                titleRes = R.string.notifications_rate_title
                canRate = !notification.wasRead
            }
        }

        tvTitle.apply {
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

        tvActionText.apply {
            isVisible = actionTextRes != null
            text = actionTextRes?.let { getString(it) }
        }

        btnRate.apply {
            isVisible = canRate
            setOnClickListener { presenter.onNotificationRateClick() }
        }

        btnAccept.apply {
            setOnClickListener {
                isAccepted = true
                presenter.onNotificationAcceptClick()
            }

//            //isVisible = canAccept
        }

        btnCancel.apply {
            //isVisible = canAccept
            setOnClickListener {
                if (isAccepted)
                    showCancelInfo()
                else {
                    presenter.onNotificationCancelClick()
                    isCanceled = true
                }
            }
        }

        /*btnChangeDecision.apply {
            isVisible = canChangeAccept
            setOnClickListener { presenter.onNotificationChangeDecisionClick() }
        }*/
    }

    private fun AppCompatButton.enableOrDisableButton(isEnabledd: Boolean) {
        isEnabled = isEnabledd
        if (isEnabledd)
            setBackgroundResource(R.drawable.background_corners)
        else
            setBackgroundResource(R.drawable.background_corners_disabled)
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

        btnAccept.enableOrDisableButton(true)
        GetMaxStateDialog(requireContext())
            .setSelectCallback { findNavController().navigate(R.id.userStateFragment) }
    }

    override fun showSuccessAccepted() {
        btnAccept.enableOrDisableButton(false)
        btnCancel.enableOrDisableButton(true)
        btnAccept.text = getString(R.string.notifications_state_accepted)
        btnCancel.text = getString(R.string.notifications_cancel)
    }

    override fun showSuccessCanceled() {
        btnCancel.enableOrDisableButton(false)
        btnAccept.enableOrDisableButton(true)
        btnCancel.text = getString(R.string.notifications_state_cancelled)
        btnAccept.text = getString(R.string.notifications_accept)
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
