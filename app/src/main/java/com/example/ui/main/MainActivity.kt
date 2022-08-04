package com.example.ui.main

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.data.models.RemoteNotification
import com.example.data.models.UserDetail
import com.example.databinding.LayoutNoInternetBinding
import com.example.extensions.dp
import com.example.interfaces.BackgroundImageFragment
import com.example.interfaces.DoNotCheckConnectionFragment
import com.example.interfaces.NavBarColorFragment
import com.example.ui.auth.authorization.AuthorizationFragment
import com.example.ui.base.BaseFragmentActivity
import com.example.ui.chat.ChatFragment
import com.example.ui.chatList.ChatListTabsFragment
import com.example.ui.event.about.old.AboutEventFragmentArgs
import com.example.ui.event.about.redesign.AboutEventFragmentNew
import com.example.ui.event.about.redesign.AboutEventFragmentNew.Companion.ABOUT_FROM_OTHER
import com.example.ui.event.allactivities.AllActivitiesFragment
import com.example.ui.event.list.recommendations.RecommendationsFragment
import com.example.ui.event.my.MyEventsFragmentNew
import com.example.ui.event.my.schedule.MyScheduleEventsFragment
import com.example.ui.event.rating.EventRatingFragmentArgs
import com.example.ui.eventTabs.EventTabsFragment
import com.example.ui.notification.NotificationFragment
import com.example.ui.notification.NotificationFragmentArgs
import com.example.ui.notification.center.NotificationsFragment
import com.example.ui.organizations.OrganizationFragmentArgs
import com.example.ui.profile.ProfileFragment
import com.example.ui.qrscanner.auth.AuthWebsiteFragmentArgs
import com.example.ui.splash.SplashFragment
import com.example.ui.state.UserState
import com.example.ui.state.max.MaxStateScreenType
import com.example.ui.stories.StoriesFragment
import com.example.ui.tags.TagsFragment
import com.example.ui.views.*
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.*
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : BaseFragmentActivity(), MainContract.View {

    private var ignoreDeeplink = false
    var invite = -1

    @InjectPresenter
    lateinit var presenter: MainPresenter

    private var mCanGoBack = true

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>

    @ProvidePresenter
    fun providePresenter(): MainPresenter = presenterProvider.get().apply {
        newMessageTitleText = getString(R.string.chat_new_message_title_text)
        photoMessageText = getString(R.string.chat_photo_message_text)
        chatAcceptMessageText = getString(R.string.chat_accepted)
    }

    private val navBarColorDefault by lazy {
        ContextCompat.getColor(this, R.color.navBarDefault)
    }


    private lateinit var mBadgeNotification: BadgeDrawable
    private lateinit var mBadgeChat: BadgeDrawable


    private val navFragmentsLifecycleCallback =
        object : FragmentManager.FragmentLifecycleCallbacks() {

            override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
                super.onFragmentPaused(fm, f)
                if (f is AboutEventFragmentNew) {
                    cancelWindowTransparency()
                }
            }

            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                super.onFragmentStarted(fm, f)
                if (f is AboutEventFragmentNew) {
                    setWindowTransparency()
                }
                if (f is SplashFragment) {
                    hideToolbar()
                }
                if (f is StoriesFragment) {
                    doEdgeWindow()
                }

            }

            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentDestroyed(fm, f)
                if (f is AboutEventFragmentNew) {
                    cancelWindowTransparency()
                }
                if (f is StoriesFragment) {
                    cancelWindowTransparency()
                    presenter.onStoriesComplete()
                }
            }

            override fun onFragmentViewCreated(
                fm: FragmentManager,
                f: Fragment,
                v: View,
                savedInstanceState: Bundle?
            ) {
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

                when (f) {
                    is AboutEventFragmentNew,
                    is RecommendationsFragment,
                    is MyEventsFragmentNew,
                    is ChatListTabsFragment,
                    is ProfileFragment,
                    is MyScheduleEventsFragment,
                    is NotificationsFragment -> showNavBar()
                    else -> hideNavBar()
                }

//                if (f is ToolbarFragment) {
//                    supportActionBar?.title = f.title
//                    (supportActionBar as? ToolbarContentActionBar)?.apply {
//                        f.setupToolbarContent(
//                            this
//                        )
//                    }
//                    showToolbar()
//                } else {
//                    hideToolbar()
//                }

                setupNavBarItems(f)

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
                    window.decorView.systemUiVisibility =
                        if (isLightStatus) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
                }

                window.navigationBarColor =
                    if (f is NavBarColorFragment) f.navBarColor else navBarColorDefault

                mBinding.root.background = bg

                if (f is SimpleTitleToolbar) {
                    mBinding.appBar.visibility = View.VISIBLE
                } else {
                    mBinding.appBar.visibility = View.GONE
                }

            }


        }

    private val backClick = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

            val fr = navHostFragment.childFragmentManager.fragments[0]
            val navContr = findNavController(R.id.navHostFragment)
            if (fr is ProfileFragment || fr is MyEventsFragmentNew || fr is NotificationsFragment || fr is ChatListTabsFragment) {
                navContr.popBackStack(R.id.recommendations_fragment, false)
            } else if (fr is TagsFragment) {
                fr.setFragmentResult("tags_fragment", bundleOf("tags" to fr.getTags()))
            } else if (fr is AllActivitiesFragment) {
                fr.setFragmentResult("all_actions", bundleOf("isUpdate" to fr.isUpdate()))
                navContr.navigateUp()
            } else if (fr is RecommendationsFragment || fr is AuthorizationFragment) {
                finish()
            } else {
                if (mCanGoBack)
                    navContr.navigateUp()
            }
        }
    }

    private var toolbarContentActionBar: ToolbarContentActionBar? = null

    private lateinit var inAppBehavior: BottomSheetBehavior<ConstraintLayout>

    private var noInternetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //setMainTheme()
        super.onCreate(savedInstanceState)
//        super.setSupportActionBar(toolbar)
//        super.getSupportActionBar()?.apply {
//            setDisplayShowCustomEnabled(true)
//            setDisplayShowTitleEnabled(false)
//            setCustomView(
//                ToolbarContentView(this@MainActivity),
//                ActionBar.LayoutParams(MATCH_PARENT, MATCH_PARENT)
//            )
//        }

        IS_EXPANDED = true
        navHostFragment.childFragmentManager.registerFragmentLifecycleCallbacks(
            navFragmentsLifecycleCallback,
            false
        )
        onBackPressedDispatcher.addCallback(this, backClick)
        setupMainNavBar()
        subscribeOnNotificationChanel()
        inAppBehavior = ScrollingChildBehavior.from(mBinding.include.inappContainer).apply {
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
        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        mBinding.ibErrorClose.setOnClickListener { presenter.onRequestHideErrorMessage() }
    }

    override fun setStartDestinationRecommendationsFragment() {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.main_navigation)
        graph.startDestination = R.id.recommendations_fragment
        navHostFragment.navController.graph = graph
    }

    override fun setStartDestinationAuthFragment() {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.main_navigation)
        graph.startDestination = R.id.authorization_fragment
        navHostFragment.navController.graph = graph
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
                val email = it.getQueryParameter(AUTH_CONFIRM_EMAIL)
                val authCode = it.getQueryParameter(AUTH_CONFIRM_EMAIL_CODE)
                val recoverEmail = it.getQueryParameter(RECOVERY_EMAIL)
                val changeEmail = it.getQueryParameter(CHANGE_EMAIL)
                val usip = it.getQueryParameter(USIP)
                val paths = it.pathSegments
                val lastPath = it.lastPathSegment

                if (lastPath == PATH_EVENT_MEMBER) {
                    val memberEmail = it.getQueryParameter(AUTH_CONFIRM_EMAIL)
                    val memberCode = it.getQueryParameter(AUTH_CONFIRM_EMAIL_CODE)
                    presenter.onInviteRegister(
                        memberEmail ?: "",
                        memberCode ?: "",
                        "",
                        "",
                        "",
                        0
                    )
                }
                if (lastPath == PATH_QR) {
                    QR_CODE_TO_AUTH_WEB = it.getQueryParameter(AUTH_CONFIRM_EMAIL_CODE) ?: ""
                    val mCode = it.getQueryParameter(AUTH_CONFIRM_EMAIL_CODE) ?: ""
                    presenter.openAuthWebsiteFragment(mCode)
                }

                if (paths.contains(PATH_EVENT) && lastPath != null) {
                    if (lastPath.contains(PATH_HIDDEN))
                        presenter.onHandleEvent(authCode ?: "")
                    else
                        presenter.onHandleEvent(lastPath)
                } else if (lastPath == PATH_CHANGE_EMAIL) {
                    /*if (changeEmail != null && authCode != null) {
                        presenter.onHandleChangeEmailConfirm(changeEmail, authCode)
                    }*/
                } else if (authEmail != null && authCode != null) {
                    //presenter.onHandleAuthLink(authEmail, authCode)
                } else if (/*authCode != null && recoverEmail != null*/lastPath == PASSWORD_RECOVERY && authCode != null) {
                    presenter.onHandleRecoverPasswordLink(/*recoverEmail, */authCode)
                } else if (lastPath == PATH_SN_AUTHORIZATION) {
                    val userId = it.getQueryParameter(FIELD_SN_AUTHORIZATION_USER_ID)
                    if (userId != null && authCode != null) {
                        presenter.onHandleSocialNetworkConfirm(userId, authCode)
                    }
                } else if (changeEmail != null && authCode != null && lastPath != PGRF) {
                    if (lastPath == REGISTER_CONFIRM) {
                        showFinishRegister(
                            "",
                            "",
                            "",
                            "",
                            email ?: "",
                            authCode,
                            false,
                            false,
                            true
                        )
                        //presenter.onHandleAuthLink(email?: "", authCode?: "")
                    } else {
                        //if (lastPath == PATH_CONFIRM_EMAIL)
                        presenter.onHandleChangeEmailConfirm(authCode ?: "", email ?: "")
                        //else presenter.onInviteRegister(changeEmail, authCode)
                    }
                } else if (lastPath == REGISTER_CONFIRM) {
                    presenter.onHandleAuthLink(email ?: "", authCode ?: "")
                } else if (lastPath == PATH_CONFIRM_EMAIL) {
                    presenter.onHandleChangeEmailConfirm(authCode ?: "", email ?: "")
                } else if (lastPath == LINKED_REGISTER) {
                    val base = Base64.decode(it.getQueryParameter("data"), Base64.DEFAULT)

                    val text = String(base, StandardCharsets.UTF_8)
                    val json = JSONObject(text)
                    presenter.onInviteRegister(
                        json["email"].toString(),
                        authCode ?: "",
                        if (json["name"].toString() != "null") json["name"].toString() else "",
                        if (json["lastName"].toString() != "null") json["lastName"].toString() else "",
                        if (json["middleName"].toString() != "null") json["middleName"].toString() else "",
                        0
                    )
                } else if (lastPath == PGRF) {
                    val base = Base64.decode(it.getQueryParameter("data"), Base64.DEFAULT)
                    val text = String(base, StandardCharsets.UTF_8)
                    val json = JSONObject(text)
                    val invite = it.getQueryParameter(AUTH_CONFIRM_INVITE_ID)
                    if (!ignoreDeeplink)
                        presenter.onInviteRegister(
                            json["email"].toString(),
                            authCode
                                ?: "",
                            if (json["name"].toString() != "null") json["name"].toString() else "",
                            if (json["lastName"].toString() != "null") json["lastName"].toString() else "",
                            if (json["middleName"].toString() != "null") json["middleName"].toString() else "",
                            invite?.toInt()
                                ?: 0
                        )
                    ignoreDeeplink = false
                }
            }
        } else {
            val extras = intent.extras ?: return

            when {
                extras.containsKey(FIELD_CHAT) -> {
                    intent.getBundleExtra(FIELD_CHAT)?.let {
                        val chatId = it.getString(FIELD_CHAT_ID, null)
                        val userName = it.getString(FIELD_LABEL, null)
                        val notificationId = it.getString(FIELD_NOTIFICATION_ID, null)
                        if (chatId != null && userName != null) {
                            presenter.onHandleChat(chatId, userName, notificationId)
                        }
                    }
                }
                extras.containsKey(FIELD_EVENT) -> {
                    val eventId = extras.getString(FIELD_EVENT)
                    if (eventId != null) {
                        presenter.onHandleEvent(eventId)
                    }
                }
                extras.containsKey(FIELD_NOTIFICATION) -> {
                    extras.getParcelable<RemoteNotification>(FIELD_NOTIFICATION)?.let {
                        presenter.onHandleNotification(it)
                    }
                }
            }
        }
    }

    fun setIgnoreDeeplink(isIgnore: Boolean) {
        ignoreDeeplink = isIgnore
    }

    override fun showInviteRegister(
        email: String,
        code: String,
        name: String,
        lastName: String,
        middleName: String,
        invite: Int
    ) {
        findNavController().navigate(
            R.id.to_invite_register, bundleOf(
                "code" to code,
                "email" to email,
                "name" to name,
                "lastName" to lastName,
                "middleName" to middleName,
                "invite" to invite
            ), NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build()
        )
    }


    private fun wasLaunchedFromResents(intent: Intent): Boolean {
        return intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
    }

    override fun checkIntent() {
        handleIntent(intent)
    }

    override fun showDialogRecoverPassword(/*email: String,*/ code: String) {
        NewPasswordDialog(this)
            .setSelectCallback {
                presenter.onSetPassword(/*email,*/ code, it)
            }
    }

    override fun showDialogChangeEmailSuccess() {
//        showDialog(getString(R.string.email_change_confirm_success))
    }

    override fun showDialogChangeEmailError() {
//        showDialog(getString(R.string.email_change_confirm_error))
    }

    override fun showDialogHasMaxState() {
        MessageDialogWithBrownButton(
            this,
            getString(R.string.you_got_max_state)
        ).setSelectCallback {}
//        BaseStateDialog(resources.getString(R.string.you_got_max_state), this)
//            .setSelectCallback {}
    }

    override fun showDialogHasBaseState() {
        MessageDialogWithBrownButton(
            this,
            getString(R.string.you_got_base_state)
        ).setSelectCallback {}
//        BaseStateDialog(resources.getString(R.string.you_got_base_state), this)
//            .setSelectCallback {}
    }

    override fun showChat(chatId: String, userName: String) {
        findNavController().navigate(
            R.id.chat_fragment, bundleOf(
                FIELD_LABEL to userName,
                FIELD_CHAT_ID to chatId
            )
        )
    }

    private fun subscribeOnNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.app_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelId, NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
    }

    override fun showGreetings() = findNavController().navigate(
        R.id.welcome_fragment, null, NavOptions.Builder()
            .setPopUpTo(R.id.main_navigation, true)
            .build()
    )

    override fun showLogin() = findNavController().navigate(
        R.id.authorization_fragment, null, NavOptions.Builder()
            .setPopUpTo(R.id.main_navigation, true)
            .build()
    )

    override fun showFinishRegister(
        name: String,
        lastName: String,
        middleName: String?,
        phone: String?,
        email: String,
        code: String,
        userPhoneConfirmed: Boolean,
        isNoMiddleName: Boolean,
        nameEditable: Boolean
    ) =
        findNavController().navigate(
            R.id.register_email_finish_fragment, bundleOf(
                "code" to code,
                "name" to name,
                "lastName" to lastName,
                "email" to email,
                "phone" to phone,
                "middleName" to middleName,
                "isConfirmed" to userPhoneConfirmed,
                "isNoMiddleName" to isNoMiddleName,
                "nameEditable" to nameEditable
            ), NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build()
        )

    override fun showRecommendations() {
        window.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.main_background)))
        if (findNavController().currentDestination?.id != R.id.register_email_finish_fragment) {
            findNavController().navigate(
                R.id.recommendations_fragment, null, NavOptions.Builder()
                    .setPopUpTo(R.id.main_navigation, true)
                    .build()
            )
            if (invite != -1) {
                presenter.openPgrfFromInvite(invite.toString())
                invite = -1
            }
        }
    }

    override fun showEvent() = findNavController().navigate(
        R.id.event_tabs_fragment, null, NavOptions.Builder()
            .setPopUpTo(R.id.main_navigation, true)
            .build()
    )

    override fun showEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment,
            AboutEventFragmentArgs.Builder(event, ABOUT_FROM_OTHER).build().toBundle()
        )
    }

    override fun showAuthWebsiteFragment(code: String) {
        findNavController().navigate(
            R.id.authWebsiteFragment,
            AuthWebsiteFragmentArgs.Builder(code).build().toBundle()
        )
    }


    override fun showStories() {
        findNavController().navigate(R.id.stories_fragment)
    }

    override fun showOrganization(organization: String) {
        findNavController().navigate(
            R.id.organization_fragment,
            OrganizationFragmentArgs.Builder(organization).build().toBundle()
        )
    }

    override fun showNotification(notification: Notification) {
        findNavController().navigate(
            R.id.notification_fragment,
            NotificationFragmentArgs.Builder(notification).build().toBundle()
        )
    }

    override fun showRating(event: String) {
        findNavController().navigate(
            R.id.event_rating_fragment,
            EventRatingFragmentArgs.Builder(event).build().toBundle()
        )
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
            .replace(mBinding.include.flNotificationContainer.id, createNotificationFragment(inapp))
            .commitNowAllowingStateLoss()

        when (inapp.type) {
            Notification.Type.SIMPLE -> {
                mBinding.include.btnPositive.apply {
                    isVisible = true
                    text = getString(R.string.ok)
                    setOnClickListener { presenter.onInappOkClick(inapp) }
                }
                mBinding.include.btnNegative.apply {
                    isVisible = false
                }
            }
            Notification.Type.ACCEPTABLE -> {
                mBinding.include.btnPositive.apply {
                    isVisible = true
                    text = getString(R.string.notifications_accept)
                    setOnClickListener { presenter.onInappAcceptClick(inapp) }
                }
                mBinding.include.btnNegative.apply {
                    isVisible = true
                    text = getString(R.string.notifications_cancel)
                    setOnClickListener { presenter.onInappCancelClick(inapp) }
                }
            }
            Notification.Type.RATE -> {

            }
        }

        mBinding.include.llButtons.doOnLayout {
            mBinding.include.flNotificationContainer.updatePadding(bottom = mBinding.include.llButtons.height - mBinding.include.llButtons.paddingTop / 2)
        }

        inAppBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun hideInapp() {
        inAppBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun createNotificationFragment(inapp: Notification): NotificationFragment {
        return NotificationFragment().apply {
            arguments = NotificationFragmentArgs.Builder(inapp).build().toBundle()
        }
    }

    private fun dimContent(dim: Boolean) {
        mBinding.include.inappDim.setBackgroundResource(if (dim) R.color.dim else 0)
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
                val dialogBinding = LayoutNoInternetBinding.inflate(layoutInflater)
                dialogBinding.apply {
                    this.btnAction.text =
                        getString(if (mustGoToEvent) R.string.no_internet_action_to_calendar else R.string.no_internet_action_retry)
                    this.btnAction.setOnClickListener {
                        if (mustGoToEvent) this@MainActivity.findNavController()
                            .popBackStack(R.id.event_tabs_fragment, false)
                        else presenter.onRetryConnectionClick()
                    }
                }
                setContentView(dialogBinding.root)
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
        mBinding.tvErrorMessage.text = message
        TransitionManager.beginDelayedTransition(mBinding.root, Slide(Gravity.TOP))
        mBinding.errorContainer.isVisible = true
    }

    override fun hideErrorMessage() {
        TransitionManager.beginDelayedTransition(mBinding.root, Slide(Gravity.TOP))
        mBinding.errorContainer.isVisible = false
    }

    override fun navigateUp() {
        onSupportNavigateUp()
    }

    override fun onSupportNavigateUp() = findNavController().navigateUp()

    override fun onBackPressed() {
        if (inAppBehavior.state == BottomSheetBehavior.STATE_HIDDEN) super.onBackPressed()
        else inAppBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun showBackButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    override fun onDestroy() {
        navHostFragment.childFragmentManager.unregisterFragmentLifecycleCallbacks(
            navFragmentsLifecycleCallback
        )
        super.onDestroy()
    }

    override fun showEmailErrorMessage() {
        ApiErrorDialog(
            this, getString(R.string.email_exist_error_title),
            getString(R.string.email_exist_error_text)
        )
            .setSelectCallback { }
    }

    override fun showPhoneErrorMessage() {
        ApiErrorDialog(
            this, getString(R.string.phone_exist_error_title),
            getString(R.string.phone_exist_error_text)
        )
            .setSelectCallback { }
    }

    override fun showErrorMessage(canGoBack: Boolean, message: String) {
    }


    override fun showStateErrorMessage(type: StateType, hasBase: Boolean, user: UserDetail?) {
        ChangeStateDialog(this, type)
            .setClickCallback {
                when (it) {
                    ClickType.INFO -> {
                        findNavController().navigate(R.id.userStateFragment)
                    }
                    ClickType.BASE -> {
                        findNavController().navigate(
                            R.id.mainInfoFragment,
                            bundleOf("type" to UserState.BASE, "screen" to 3)
                        )
                    }
                    ClickType.MAX -> {
                        if (presenter.getHasBase()) {
                            when (Utils.maxStateScreen(presenter.getUserData())) {
                                MaxStateScreenType.BASE ->
                                    findNavController().navigate(
                                        R.id.maxStateMainInfoFragment,
                                        bundleOf("screen" to 1)
                                    )
                                MaxStateScreenType.INTERESTS ->
                                    findNavController().navigate(
                                        R.id.baseStateInterestsFragment,
                                        bundleOf("screen" to 1)
                                    )
                                MaxStateScreenType.WORK ->
                                    findNavController().navigate(
                                        R.id.maxStateWorkFragment,
                                        bundleOf("screen" to 1)
                                    )
                                MaxStateScreenType.EDUCATION ->
                                    findNavController().navigate(
                                        R.id.maxStateEducationFragment,
                                        bundleOf("screen" to 1)
                                    )
                            }
                        } else {
                            findNavController().navigate(
                                R.id.mainInfoFragment,
                                bundleOf("type" to UserState.MAX, "screen" to 1)
                            )
                        }
                    }
                }
            }
    }

    override fun setAppBarElevation(value: Float) {
        mBinding.appBar.apply {
            elevation = if (value <= 10f) {
                value
            } else {
                10f
            }
        }
    }

    override fun setToolbarTitle(title: String) {
        mBinding.toolbarLabel.text = title
    }

    override fun showNotificationErrorMessage() {
        FillProfileDialog(this).setSelectCallback { findNavController().navigate(R.id.user_profile_fragment) }
    }

    fun setIgnoreTokenListener(isIgnore: Boolean) {
        presenter.ignoreTokenListener(isIgnore)
    }

    fun startEditPhoneListener(value: Boolean) {
        presenter.isEditingPhone = value
    }

    private fun setupMainNavBar() {
        val navController = findNavController(R.id.navHostFragment)
        mBinding.mainNavBar.setupWithNavController(navController)
        mBinding.mainNavBar.setOnNavigationItemReselectedListener { }
        mBinding.mainNavBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main -> {
                    findNavController(R.id.navHostFragment).popBackStack(
                        R.id.recommendations_fragment,
                        false
                    )
                    true
                }
                R.id.my_events -> {
                    findNavController(R.id.navHostFragment).navigate(R.id.my_events_fragment_new)
                    true
                }
                R.id.chats -> {
                    findNavController(R.id.navHostFragment).navigate(R.id.chat_list_tabs_fragment)
                    true
                }
                R.id.notification -> {
                    findNavController(R.id.navHostFragment).navigate(R.id.notifications_fragment)
                    true
                }
                R.id.profile -> {
                    findNavController(R.id.navHostFragment).navigate(R.id.profile_fragment)
                    true
                }
                else -> false
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splash_fragment -> {
                    hideNavBar()
                }
            }
        }

        mBadgeNotification = mBinding.mainNavBar.getOrCreateBadge(R.id.notification)
        mBadgeNotification.backgroundColor = Color.RED
        mBadgeChat = mBinding.mainNavBar.getOrCreateBadge(R.id.chats)
        mBadgeChat.backgroundColor = Color.RED
    }


    override fun showBadgeNotification(show: Boolean) {
        mBadgeNotification.isVisible = show
        //showNotificationBadge(show)
    }

    override fun showBadgeChat(show: Boolean) {
        mBadgeChat.isVisible = show
    }


    private fun setupNavBarItems(f: Fragment) {
        when (f) {
            is RecommendationsFragment -> {
                mBinding.mainNavBar.selectedItemId =
                    mBinding.mainNavBar.menu.findItem(R.id.main).itemId
            }
            is ProfileFragment -> {
                mBinding.mainNavBar.selectedItemId =
                    mBinding.mainNavBar.menu.findItem(R.id.profile).itemId
            }
            is ChatListTabsFragment -> {
                mBinding.mainNavBar.selectedItemId =
                    mBinding.mainNavBar.menu.findItem(R.id.chats).itemId
            }
            is NotificationsFragment -> {
                mBinding.mainNavBar.selectedItemId =
                    mBinding.mainNavBar.menu.findItem(R.id.notification).itemId
            }
        }
    }

    fun showNavBar() {
        mBinding.navBarContainer.visibility = View.VISIBLE
    }

    fun hideNavBar() {
        mBinding.navBarContainer.visibility = View.GONE
    }

    override fun getLoadingView(): View {
        mBinding.progressWhiteBackground.isVisible = true
        return mBinding.flLoading
    }

    override fun getProgressBarLoadingView(): View {
        mBinding.progressWhiteBackground.isVisible = false
        return mBinding.flLoading
    }

    override fun addProgressView() {
        val progressBar = CustomProgressView(this@MainActivity)
        progressBar.setSize(35.dp)
        progressBar.setProgressColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.main_brown_color_new
            )
        )
        mBinding.progressViewContainer.addView(progressBar, 0)
    }

    override fun enableBackClickListener() {
        mCanGoBack = true
    }

    override fun disableBackClickListener(){
        mCanGoBack = false
    }


    override fun removeProgressView() {
        mBinding.progressViewContainer.removeAllViews()
    }

    override fun layout() = R.layout.activity_main

    override fun setMainTheme() {
        window.navigationBarColor =
            navBarColorDefault
        setTheme(R.style.AppTheme)
    }
}
