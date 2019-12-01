package com.example.ui.main

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.data.models.RemoteNotification
import com.example.interfaces.BackgroundImageFragment
import com.example.interfaces.DoNotCheckConnectionFragment
import com.example.interfaces.ToolbarFragment
import com.example.ui.auth.authorization.AuthorizationFragment
import com.example.ui.base.BaseFragmentActivity
import com.example.ui.chat.ChatFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.list.recommendations.RecommendationsFragment
import com.example.ui.eventTabs.EventTabsFragment
import com.example.ui.notification.NotificationFragment
import com.example.ui.notification.NotificationFragmentArgs
import com.example.ui.organizations.OrganizationFragmentArgs
import com.example.ui.splash.SplashFragment
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.ui.views.toolbar.ToolbarContentView
import com.example.util.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_password_recovery.view.*
import kotlinx.android.synthetic.main.item_action_button.view.*
import kotlinx.android.synthetic.main.layout_inapp.*
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : BaseFragmentActivity(), MainContract.View {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>

    @ProvidePresenter
    fun providePresenter(): MainPresenter = presenterProvider.get().apply {
        newMessageTitleText = getString(R.string.chat_new_message_title_text)
        photoMessageText = getString(R.string.chat_photo_message_text)
        chatAcceptMessageText = getString(R.string.chat_accepted)
    }

    private val navFragmentsLifecycleCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            presenter.apply {
                when (f) {
                    is ChatFragment -> presenter.onOpenChatDestination(f.chatId)
                    is SplashFragment,
                    is AuthorizationFragment,
                    is RecommendationsFragment,
                    is EventTabsFragment -> onOpenStartDestination()
                    else -> onOpenNotStartDestination()
                }

                onOpenCheckConnectionDestination(f is DoNotCheckConnectionFragment)
            }

            if (f is ToolbarFragment) {
                supportActionBar?.title = f.title
                (supportActionBar as? ToolbarContentActionBar)?.apply { f.setupToolbarContent(this) }
                showToolbar()
            } else {
                hideToolbar()
            }

            val isLightStatus: Boolean
            val bg: Drawable?
            if (f is BackgroundImageFragment) {
                bg = f.getFragmentBackgroundDrawable()
                isLightStatus = f.isLightStatus
            } else {
                bg = null
                isLightStatus = true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = if (isLightStatus) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
            }

            root.background = bg
        }
    }

    private var toolbarContentActionBar: ToolbarContentActionBar? = null

    private lateinit var inappBehavior: BottomSheetBehavior<ConstraintLayout>

    private var noInternetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setSupportActionBar(toolbar)
        super.getSupportActionBar()?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setCustomView(ToolbarContentView(this@MainActivity), ActionBar.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
        navHostFragment.childFragmentManager.registerFragmentLifecycleCallbacks(navFragmentsLifecycleCallback, false)
        subscribeOnNotificationChanel()
        inappBehavior = ScrollingChildBehavior.from(inappContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            dimContent(false)
                            presenter.onInappHidden()
                        }
                        else -> dimContent(true)
                    }
                }
            })
        }

        ibErrorClose.setOnClickListener { presenter.onRequestHideErrorMessage() }
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        throw UnsupportedOperationException("Do not set toolbars, use custom toolbar view instead")
    }

    override fun getSupportActionBar(): ActionBar? {
        return super.getSupportActionBar()?.let {
            if (toolbarContentActionBar == null) {
                toolbarContentActionBar = ToolbarContentActionBar(this, it)
            }

            toolbarContentActionBar
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (wasLaunchedFromResents(intent)) return

        val appLinkAction = intent.action
        if (Intent.ACTION_VIEW == appLinkAction) {
            intent.data?.also {
                val authEmail = it.getQueryParameter(AUTH_CONFIRM_EMAIL_EMAIL)
                val authCode = it.getQueryParameter(AUTH_CONFIRM_EMAIL_CODE)
                val recoverEmail = it.getQueryParameter(RECOVERY_EMAIL)
                val change = it.getQueryParameter(CHANGE_EMAIL)

                if (change != null) {
                    if (authEmail != null && authCode != null) {
                        presenter.onHandleChangeEmailConfirm(authEmail, authCode)
                    }
                } else if (authEmail != null && authCode != null) {
                    presenter.onHandleAuthLink(authEmail, authCode)
                } else if (authCode != null && recoverEmail != null) {
                    presenter.onHandleRecoverPasswordLink(recoverEmail, authCode)
                } else if (it.getQueryParameter(SET_EMAIL_USER_SOCIAL) != null) {
                    val id = it.getQueryParameter(ID)
                    val snType = it.getQueryParameter(SN_PROVIDER)
                    if (snType != null && id != null && authCode != null) {
                        presenter.onHandleSocialNetworkConfirm(snType, id, authCode)
                    }
                }
            }
        } else {
            val extras = intent.extras ?: return

            if (extras.containsKey(FIELD_CHAT)) {
                intent.getBundleExtra(FIELD_CHAT)?.let {
                    val chatId = it.getString(FIELD_CHAT_ID, null)
                    val userName = it.getString(FIELD_LABEL, null)
                    val notificationId = it.getString(FIELD_NOTIFICATION_ID, null)
                    if (chatId != null && userName != null) {
                        presenter.onHandleChat(chatId, userName, notificationId)
                    }
                }
            } else if (extras.containsKey(FIELD_NOTIFICATION)) {
                intent.getParcelableExtra<RemoteNotification>(FIELD_NOTIFICATION)?.let {
                    presenter.onHandleNotification(it)
                }
            }
        }
    }

    private fun wasLaunchedFromResents(intent: Intent): Boolean {
        return intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
    }

    override fun checkIntent() {
        handleIntent(intent)
    }

    override fun showDialogRecoverPassword(email: String, code: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_password_recovery, null, false)
        val alert = AlertDialog.Builder(this)
                .setTitle(R.string.recovery_set_password_title)
                .setView(view)
                .create()

        var password = ""
        var passwordConfirm = ""

        val validatePassword = {
            val isPasswordValid = AuthValidateUtil.isValidPassword(password)
            val isPasswordsMatch = password == passwordConfirm
            view.tvPasswordHintLength.apply {
                if (isPasswordValid) highlightCorrect()
                else highlightError()
            }
            view.btnSave.isEnabled = isPasswordValid && isPasswordsMatch
        }

        view.btnSave.isEnabled = false

        view.etPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            password = charSequence.toString()
            validatePassword()
        })

        view.etConfirmPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            passwordConfirm = charSequence.toString()
            validatePassword()
        })

        view.btnSave.setOnClickListener {
            alert.dismiss()
            presenter.onSetPassword(email, code, password)
        }

        alert.show()
    }

    override fun showDialogChangeEmailSuccess() {
//        showDialog(getString(R.string.email_change_confirm_success))
    }

    override fun showDialogChangeEmailError() {
//        showDialog(getString(R.string.email_change_confirm_error))
    }

    override fun showChat(chatId: String, userName: String) {
        findNavController().navigate(R.id.chat_fragment, bundleOf(
                FIELD_LABEL to userName,
                FIELD_CHAT_ID to chatId
        ))
    }

    private fun subscribeOnNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.app_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(NotificationChannel(channelId,
                    channelId, NotificationManager.IMPORTANCE_HIGH))
        }
    }

    override fun showGreetings() = findNavController().navigate(R.id.welcome_fragment, null, NavOptions.Builder()
            .setPopUpTo(R.id.main_navigation, true)
            .build())

    override fun showLogin() = findNavController().navigate(R.id.authorization_fragment, null, NavOptions.Builder()
            .setPopUpTo(R.id.main_navigation, true)
            .build())

    override fun showRecommendations() = findNavController().navigate(R.id.recommendations_fragment, null, NavOptions.Builder()
            .setPopUpTo(R.id.main_navigation, true)
            .build())

    override fun showEvent() = findNavController().navigate(R.id.event_tabs_fragment, null, NavOptions.Builder()
            .setPopUpTo(R.id.main_navigation, true)
            .build())

    override fun showEvent(event: String) {
        findNavController().navigate(R.id.about_event_fragment, AboutEventFragmentArgs.Builder(event).build().toBundle())
    }

    override fun showStories() {
        findNavController().navigate(R.id.stories_fragment)
    }

    override fun showOrganization(organization: String) {
        findNavController().navigate(R.id.organization_fragment, OrganizationFragmentArgs.Builder(organization).build().toBundle())
    }

    override fun showNotification(notification: Notification) {
        findNavController().navigate(R.id.notification_fragment, NotificationFragmentArgs.Builder(notification).build().toBundle())
    }

    private fun findNavController() = findNavController(R.id.navHostFragment)

    override fun showToolbar() {
        supportActionBar?.show()
    }

    override fun hideToolbar() {
        supportActionBar?.apply {
            title = ""
            hide()
            if (this is ToolbarContentActionBar) {
                removeAllLeftViews()
                removeAllRightViews()
            }
        }
    }

    override fun showInapp(inapp: Notification) {
        supportFragmentManager.beginTransaction()
                .replace(flNotificationContainer.id, createNotificationFragment(inapp))
                .commitNowAllowingStateLoss()

        when (inapp.type) {
            Notification.Type.SIMPLE -> {
                btnPositive.apply {
                    isVisible = true
                    text = getString(R.string.ok)
                    setOnClickListener { presenter.onInappOkClick() }
                }
                btnNegative.apply {
                    isVisible = false
                }
            }
            Notification.Type.ACCEPTABLE -> {
                btnPositive.apply {
                    isVisible = true
                    text = getString(R.string.notifications_accept)
                    setOnClickListener { presenter.onInappAcceptClick(inapp) }
                }
                btnNegative.apply {
                    isVisible = true
                    text = getString(R.string.notifications_cancel)
                    setOnClickListener { presenter.onInappCancelClick(inapp) }
                }
            }
            Notification.Type.RATE -> {

            }
        }

        llButtons.doOnLayout {
            flNotificationContainer.updatePadding(bottom = llButtons.height - llButtons.paddingTop / 2)
        }

        inappBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun hideInapp() {
        inappBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun createNotificationFragment(inapp: Notification): NotificationFragment {
        return NotificationFragment().apply {
            arguments = NotificationFragmentArgs.Builder(inapp).apply { showButtons = false }.build().toBundle()
        }
    }

    private fun dimContent(dim: Boolean) {
        inappDim.setBackgroundResource(if (dim) R.color.dim else 0)
    }

    @SuppressLint("InflateParams")
    override fun showNoConnectionMessage(show: Boolean) {
        if (show && noInternetDialog?.isShowing != true) {
            val mustGoToEvent = try {
                findNavController().getBackStackEntry(R.id.event_tabs_fragment)
                true
            } catch (e: IllegalArgumentException) {
                false
            }
            BottomSheetDialog(this).apply {
                val layout = LayoutInflater.from(this@MainActivity).inflate(R.layout.layout_no_internet, null).apply {
                    this.btnAction.text = getString(if (mustGoToEvent) R.string.no_internet_action_to_calendar else R.string.no_internet_action_retry)
                    this.btnAction.setOnClickListener {
                        if (mustGoToEvent) this@MainActivity.findNavController().popBackStack(R.id.event_tabs_fragment, false)
                        else presenter.onRetryConnectionClick()
                    }
                }
                setContentView(layout)
                setCancelable(false)
                setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) finish()
                    true
                }
                noInternetDialog = this
            }.show()
        } else if (!show) {
            noInternetDialog?.dismiss()
        }
    }

    override fun showRequestErrorMessage() {
        presenter.onRequestShowErrorMessage(getString(R.string.request_execution_error))
    }

    override fun showErrorMessage(message: String) {
        tvErrorMessage.text = message
        TransitionManager.beginDelayedTransition(root, Slide(Gravity.TOP))
        errorContainer.isVisible = true
    }

    override fun hideErrorMessage() {
        TransitionManager.beginDelayedTransition(root, Slide(Gravity.TOP))
        errorContainer.isVisible = false
    }

    override fun navigateUp() {
        onSupportNavigateUp()
    }

    override fun onSupportNavigateUp() = findNavController().navigateUp()

    override fun onBackPressed() {
        if (inappBehavior.state == BottomSheetBehavior.STATE_HIDDEN) super.onBackPressed()
        else inappBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun showBackButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    override fun onDestroy() {
        navHostFragment.childFragmentManager.unregisterFragmentLifecycleCallbacks(navFragmentsLifecycleCallback)
        super.onDestroy()
    }

    override fun getLoadingView(): View = flLoading

    override fun layout() = R.layout.activity_main
}
