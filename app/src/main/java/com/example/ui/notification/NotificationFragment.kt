package com.example.ui.notification

import android.content.Intent
import android.net.Uri
import android.text.util.Linkify
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
import kotlinx.android.synthetic.main.fragment_notification.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import javax.inject.Inject
import javax.inject.Provider

class NotificationFragment : BaseFragment(), NotificationContract.View, ToolbarFragment {

    override val title: CharSequence = ""

    @InjectPresenter
    lateinit var presenter: NotificationPresenter

    @Inject
    lateinit var presenterProvider: Provider<NotificationPresenter>

    @ProvidePresenter
    fun providePresenter(): NotificationPresenter = presenterProvider.get().apply {
        NotificationFragmentArgs.fromBundle(arguments!!).let {
            notification = it.notification
            showButtons = it.showButtons
        }
    }

    private val linkClickListener = BetterLinkMovementMethod.OnLinkClickListener { _, url ->
        presenter.onNotificationUrlClick(url)
        true
    }

    override fun setData(notification: Notification, showButtons: Boolean) {
        tvDate.apply {
            val parsedDate = notification.date.parseAndFormat(defaultServerDateTimeFormatter, defaultDateTimeFormatter)
            text = parsedDate
        }

        tvMessage.apply {
            text = notification.message?.parseAsHtml()
            BetterLinkMovementMethod.linkify(Linkify.ALL, this)
                    .setOnLinkClickListener(linkClickListener)
        }

        val titleRes: Int
        var actionTextRes: Int? = null
        var canRate = false
        var canAccept = false
        var canChangeAccept = false

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
                        canChangeAccept = showButtons
                        actionTextRes = R.string.notifications_state_accepted
                    }
                    Notification.AcceptState.CANCELED -> {
                        canChangeAccept = showButtons
                        actionTextRes = R.string.notifications_state_cancelled
                    }
                    else -> {
                        canAccept = showButtons
                    }
                }
            }
            Notification.Type.RATE -> {
                titleRes = R.string.notifications_rate_title
                canRate = showButtons && !notification.wasRead
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
            isVisible = canAccept
            setOnClickListener { presenter.onNotificationAcceptClick() }
        }

        btnCancel.apply {
            isVisible = canAccept
            setOnClickListener { presenter.onNotificationCancelClick() }
        }

        btnChangeDecision.apply {
            isVisible = canChangeAccept
            setOnClickListener { presenter.onNotificationChangeDecisionClick() }
        }

        divider.isVisible = showButtons
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
