package com.example.ui.notification

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.util.Linkify
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.parseAsHtml
import androidx.core.text.toSpannable
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
import com.example.ui.views.CtpDialog
import kotlinx.android.synthetic.main.fragment_notification.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import javax.inject.Inject
import javax.inject.Provider

class NotificationFragment : BaseFragment(), NotificationContract.View, ToolbarFragment {

    override val title: CharSequence? = null

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
        tvDate.apply {
            val parsedDate = notification.date.parseAndFormat(defaultServerDateTimeFormatter, defaultDateTimeFormatter)
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
                            findNavController().navigate(NotificationFragmentDirections.notificationToOrganizationFragment(organizationId))
                            Log.INFO
                        } else if (url.contains("event")) {
                            val eventId = url.replace("event", "").replace("/", "")
                            findNavController().navigate(NotificationFragmentDirections.notificationToAboutEventFragment(eventId))
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
        //var canAccept = false
        //var canChangeAccept = false

        when (notification.type) {
            Notification.Type.SIMPLE -> {
                titleRes = R.string.notifications_simple_title
            }
            Notification.Type.ACCEPTABLE -> {
                titleRes = R.string.notifications_acceptable_title
                when (notification.acceptState) {
                    Notification.AcceptState.DISABLED -> {
                        actionTextRes = R.string.notifications_state_disabled
                    }
                    Notification.AcceptState.ACCEPTED -> {
                        isAccepted = true
                        //canChangeAccept = true
                        actionTextRes = R.string.notifications_state_accepted
                    }
                    Notification.AcceptState.CANCELED -> {
                        //canChangeAccept = true
                        //actionTextRes = R.string.notifications_state_cancelled
                    }
                    else -> {
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
            text = getString(titleRes)
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
            //isVisible = canAccept
            setOnClickListener { presenter.onNotificationAcceptClick() }
        }

        btnCancel.apply {
            //isVisible = canAccept
            setOnClickListener {
                if (isAccepted)
                    showCancelInfo()
                else
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

    override fun showRating(eventId: String) {
        findNavController().navigate(NotificationFragmentDirections.notificationToEventRating(eventId))
    }

    override fun layout() = R.layout.fragment_notification
}
