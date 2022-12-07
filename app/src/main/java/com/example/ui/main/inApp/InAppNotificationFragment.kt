package com.example.ui.main.inApp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.data.models.UserDetail
import com.example.databinding.BottomSheetInAppNotificationBinding
import com.example.extensions.defaultDateTimeFormatter
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.parseAndFormat
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.notification.NotificationFragmentDirections
import com.example.ui.organizations.redesign.OrganizationFragmentNewArgs
import com.example.ui.profile.data.ProfileDataContract
import com.example.ui.profile.shortName.ChangeShortNameFragment
import com.example.ui.userprofile.read.settings.change_email.ChangeEmailFragment
import com.example.ui.userprofile.read.settings.change_email.ChangeEmailPresenter
import com.example.util.ScrollingChildBehavior
import com.google.android.gms.common.util.ArrayUtils.contains
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider

class InAppNotificationFragment(val inApp: Notification) :
    BaseBottomSheetFragment<BottomSheetInAppNotificationBinding>(),
    InAppNotificationContract.View {

    private var acceptAction: () -> Unit = {}
    private var cancelAction: () -> Unit = {}
    private var simpleAction: () -> Unit = {}

    @InjectPresenter(type = PresenterType.WEAK, tag = ChangeEmailFragment.CHANGE_EMAIL_FRAGMENT_TAG)
    lateinit var presenter: InAppNotificationPresenter

    @Inject
    lateinit var presenterProvider: Provider<InAppNotificationPresenter>

    @ProvidePresenter(
        type = PresenterType.WEAK,
        tag = ChangeEmailFragment.CHANGE_EMAIL_FRAGMENT_TAG
    )
    fun providePresenter(): InAppNotificationPresenter = presenterProvider.get().apply {
        this.notification = inApp
    }

    private lateinit var inAppBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    override fun setNotification(notification: Notification) {
        mBinding.apply {
            setFadeShowAnimation(bottomSheet)
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
                        when {
                            url.contains("https") || url.contains("http") -> {
                                val i = Intent(Intent.ACTION_VIEW)
                                i.data = Uri.parse(url)
                                startActivity(i)
                                true
                            }
                            url.contains("organization") -> {
                                val organizationId = getIdFromUrl("organization", url)
                                showAboutOrganization(organizationId)
                                true
                            }
                            url.contains("event") -> {
                                val eventId = getIdFromUrl("event", url)
                                showAboutEvent(eventId)
                                true
                            }
                            else -> false
                        }
                    }
            }
            var titleRes = ""

            when (notification.type) {
                Notification.Type.SIMPLE -> {
                    titleRes = getString(R.string.notifications_simple_title)
                    btnCancel.isVisible = false
                    btnAccept.apply {
                        isVisible = true
                        text = getString(R.string.ok)
                        setOnClickListener {
                            simpleAction.invoke()
                        }
                    }
                }
                Notification.Type.ACCEPTABLE -> {
                    titleRes = getString(R.string.notifications_acceptable_title)
                    when (notification.acceptState) {
                        Notification.AcceptState.DISABLED -> {
                            btnAccept.isVisible = false
                            btnCancel.isVisible = false
                            mBinding.tvActionText.apply {
                                isVisible = true
                                text = getString(R.string.notifications_state_disabled)
                            }
                        }
                        Notification.AcceptState.ACCEPTED -> {
                            mBinding.apply {
                                btnAccept.apply {
                                    isVisible = true
                                    isEnabled = false
                                    text = getString(R.string.notifications_state_accepted)
                                }
                                btnCancel.apply {
                                    isVisible = true
                                    isEnabled = true
                                    setOnClickListener {
                                        cancelAction.invoke()
                                    }
                                }
                            }
                        }
                        Notification.AcceptState.CANCELED -> {
                            mBinding.apply {
                                btnAccept.apply {
                                    isVisible = true
                                    isEnabled = true
                                    setOnClickListener {
                                        acceptAction.invoke()
                                    }
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
                    titleRes = getString(R.string.notifications_rate_title)
                    mBinding.btnCancel.isVisible = false
                    mBinding.btnAccept.apply {
                        isVisible = !notification.wasRead
                        text = getString(R.string.notifications_rate_event)
                    }
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
                            val eventId = notification.eventId.toString()
                            showAboutEvent(eventId)
                            true
                        }
                    removeUrlUnderline()
                } else {
                    text = titleRes
                }
            }
        }
    }

    override fun showAboutEvent(eventId: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(eventId).build().toBundle()
        )
    }

    override fun showAboutOrganization(organizationId: String) {
        findNavController().navigate(
            R.id.organization_fragment_new,
            OrganizationFragmentNewArgs.Builder(organizationId).build().toBundle()
        )
    }


    private fun getIdFromUrl(prefix: String, url: String): String {
        return url.replace(prefix, "").replace("/", "")
    }


    private fun setFadeShowAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 350
        view.startAnimation(anim)
    }

    private fun setFadeHideAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 450
        view.startAnimation(anim)
    }

    fun setSimpleActionCallback(block: () -> Unit): InAppNotificationFragment {
        simpleAction = block
        return this
    }


    override fun layout(): Int = R.layout.bottom_sheet_in_app_notification
}