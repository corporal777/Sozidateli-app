package com.example.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.example.R
import com.example.data.models.Notification
import com.example.data.models.RemoteNotification
import com.example.data.models.SupportData
import com.example.data.models.UserDetail
import com.example.databinding.LayoutNoInternetBinding
import com.example.extensions.decodeBase64ToJson
import com.example.interfaces.DoNotCheckConnectionFragment
import com.example.interfaces.ToolbarFragment
import com.example.ui.auth.authorization.AuthorizationFragment
import com.example.ui.auth.recoveryPassword.RecoveryPasswordFragmentArgs
import com.example.ui.base.BaseFragmentActivity
import com.example.ui.chatList.ChatListTabsFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.list.recommendations.RecommendationsFragment
import com.example.ui.event.list.recommendations.RecommendationsFragmentArgs
import com.example.ui.event.my.MyEventsFragment
import com.example.ui.event.my.schedule.MyScheduleEventsFragment
import com.example.ui.event.rating.EventRatingFragmentArgs
import com.example.ui.main.inApp.InAppNotificationFragment
import com.example.ui.notification.NotificationsListFragment
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.ui.profile.ProfileFragment
import com.example.ui.qrscanner.auth.AuthWebsiteFragmentArgs
import com.example.ui.state.UserState
import com.example.ui.state.maxNew.MaxStateScreenType
import com.example.ui.stories.StoriesFragment
import com.example.ui.support.detail.SupportQuestionDetailFragmentArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.ApiErrorDialog
import com.example.ui.views.dialogs.ChangeStateBottomDialog
import com.example.ui.views.dialogs.ClickType
import com.example.ui.views.dialogs.StateType
import com.example.ui.views.dialogs.UpdateAppBottomSheet
import com.example.ui.views.toolbar.CustomAppBarLayoutBehavior
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.ASSISTANT
import com.example.util.AUTH_CONFIRM_EMAIL
import com.example.util.AUTH_CONFIRM_EMAIL_CODE
import com.example.util.AUTH_CONFIRM_INVITE_ID
import com.example.util.AUTH_CONFIRM_SOCKET_ID
import com.example.util.FIELD_CHAT
import com.example.util.FIELD_CHAT_ID
import com.example.util.FIELD_EVENT
import com.example.util.FIELD_LABEL
import com.example.util.FIELD_NAME
import com.example.util.FIELD_NOTIFICATION
import com.example.util.FIELD_NOTIFICATION_ID
import com.example.util.LINKED_REGISTER
import com.example.util.PASSWORD_RECOVERY
import com.example.util.PATH_AUTH
import com.example.util.PATH_EVENT
import com.example.util.PATH_EVENT_MEMBER
import com.example.util.PATH_HIDDEN
import com.example.util.PATH_LP
import com.example.util.PATH_PROFILE
import com.example.util.PATH_QR
import com.example.util.PATH_SETTINGS
import com.example.util.PATH_SUPPORT_CENTER
import com.example.util.PATH_SWITCH_ACCOUNT
import com.example.util.PATH_USER
import com.example.util.PGRF
import com.example.util.Utils
import com.example.util.showCustomTabsBrowser
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.extensions.getFragmentLifecycleCallback
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.onBackPressedCallback
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : BaseFragmentActivity(), MainContract.View {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>

    @ProvidePresenter
    fun providePresenter(): MainPresenter = presenterProvider.get()

    private val navBarColorDefault by lazy { getColor(R.color.main_background) }
    private val navBarColorBottomNav by lazy { getColor(R.color.bottom_navigation_view_background_color) }

    private val navFragmentsLifecycleCallback = getFragmentLifecycleCallback(
        onFragmentStopped = { },
        onFragmentDestroyed = { f -> if (f is StoriesFragment) presenter.onStoriesComplete() },
        onFragmentStarted = { f -> setupBackgroundTransparency(f) },
        onBottomSheetViewCreated = { },
        onFragmentViewCreated = { f ->
            when (f) {
                is RecommendationsFragment,
                is MyEventsFragment,
                is ChatListTabsFragment,
                is ProfileFragment,
                is MyScheduleEventsFragment,
                is NotificationsListFragment -> showNavBar()
                else -> hideNavBar()
            }

            presenter.onOpenCheckConnectionDestination(f is DoNotCheckConnectionFragment)
            setupNavBarItems(f)
            setupBackgroundImageFragment(f)

            mBinding.appBar.isVisible = f is ToolbarFragment
            if (f is ToolbarFragment) {
                mBinding.toolbar.apply {
                    toolbarLabel.text = f.title
                    f.setupToolbarContent(ToolbarContent(ivBack, toolbarLabel, toolbarContainer))
                    f.actionIconContainer(toolbarContainer)
                }
                getBehavior()?.setScrollChangeCallback {
                    f.scrollValue(it)
                    presenter.changeScrollingOffset(it)
                }
            }
        }
    )

    private val backClick = onBackPressedCallback(true) {
        val fragment = getNavHostFragment().childFragmentManager.fragments.firstOrNull() ?: return@onBackPressedCallback
        when (fragment) {
            is ProfileFragment,
            is MyEventsFragment,
            is NotificationsListFragment,
            is ChatListTabsFragment -> findNavController().popBackStack(R.id.recommendations_fragment, false)
            is RecommendationsFragment,
            is AuthorizationFragment -> {
                if (isPreviousDestination(R.id.change_account_fragment)) navigateUp() else finish()
            }
            else -> navigateUp()
        }
    }

    private var noInternetDialog: BottomSheetDialog? = null
    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        showSplashScreen()
        super.onCreate(savedInstanceState)

        registerFragmentLifecycleCallback()
        onBackPressedDispatcher.addCallback(this, backClick)
        setupMainNavBar()
        subscribeOnNotificationChanel()

        mBinding.toolbar.ivBack.setOnClickListener { navigateUp() }
        mBinding.ibErrorClose.setOnClickListener { presenter.onRequestHideErrorMessage() }
    }


    override fun showSplashScreen() {
        splashScreen = installSplashScreen().apply { setKeepVisibleCondition { true } }
    }

    override fun hideSplashScreen() {
        splashScreen.setKeepVisibleCondition { false }
    }

    private fun wasLaunchedFromResents(intent: Intent): Boolean {
        val fromHistory = Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
        return intent.flags and fromHistory == fromHistory
    }

    override fun checkIntent() = handleIntent(intent)
    override fun clearIntentData() = intent.let { it.data = null }

    override fun handleIntent(intent: Intent) {
        if (wasLaunchedFromResents(intent)) return
        if (Intent.ACTION_VIEW == intent.action) {
            intent.data?.also {
                val authCode = it.getQueryParameter(AUTH_CONFIRM_EMAIL_CODE)
                val paths = it.pathSegments
                val lastPath = it.lastPathSegment

                //catch path auth
                if (lastPath == PATH_AUTH || lastPath == PATH_SWITCH_ACCOUNT) {
                    val redirectLink = it.getQueryParameter("redirect")
                    presenter.onHandleAuthToOtherPlatform(redirectLink)
                }
                //catch path qr code
                else if (lastPath == PATH_QR) {
                    val code = it.getQueryParameter(AUTH_CONFIRM_EMAIL_CODE)
                    val id = it.getQueryParameter(AUTH_CONFIRM_SOCKET_ID)
                    presenter.onHandleAuthWebsite(code, id)
                }
                //catch path event
                else if (!lastPath.isNullOrEmpty() && paths.contains(PATH_EVENT)) {
                    if (lastPath.contains(PATH_HIDDEN)) presenter.onHandleEventCode(authCode)
                    else presenter.onHandleEvent(lastPath)
                }
                //catch path profile settings
                else if (lastPath == PATH_SETTINGS) presenter.onHandleProfileSettingsLink()
                //catch path profile
                else if (lastPath == PATH_PROFILE) presenter.onHandleProfileLink()
                //catch path user
                else if (paths.contains(PATH_USER) && !lastPath.isNullOrBlank()) {
                    presenter.onHandleUser(lastPath)
                }
                //catch path support center question
                else if (lastPath == PATH_SUPPORT_CENTER) {
                    val question = it.getQueryParameter("question")
                    presenter.onHandleSupportQuestionLink(question)
                }
                //catch path recover password
                else if (lastPath == PATH_LP) {
                    if (it.fragment == "recover-password") presenter.onHandleRecoverPasswordLink()
                }
                //catch path password change
                else if (lastPath == PASSWORD_RECOVERY && authCode != null) {
                    val indexLastPath = paths.indexOf(lastPath)
                    val userId = if (indexLastPath > 0) paths[indexLastPath - 1] else ""
                    presenter.onHandleChangePasswordLink(userId, authCode)
                }
                //catch path event member
                else if (lastPath == PATH_EVENT_MEMBER) {
                    val memberEmail = it.getQueryParameter(AUTH_CONFIRM_EMAIL)
                    val memberCode = it.getQueryParameter(AUTH_CONFIRM_EMAIL_CODE)
                    presenter.onInviteRegister(
                        memberEmail ?: "",
                        memberCode ?: "",
                        "",
                        "",
                        "",
                        -1
                    )
                }
                //catch path pgrf, assistant
                else if (lastPath == PGRF || lastPath == ASSISTANT || lastPath == LINKED_REGISTER) {
                    val json = decodeBase64ToJson(it.getQueryParameter("data")) ?: return
                    val invite = it.getQueryParameter(AUTH_CONFIRM_INVITE_ID)

                    presenter.onInviteRegister(
                        json["email"].toString(),
                        authCode ?: "",
                        if (json["name"].toString() != "null") json["name"].toString() else "",
                        if (json["lastName"].toString() != "null") json["lastName"].toString() else "",
                        if (json["middleName"].toString() != "null") json["middleName"].toString() else "",
                        invite?.toInt() ?: -1
                    )
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
                        if (chatId != null && userName != null && notificationId != null) {
                            presenter.onHandleChat(chatId, userName, notificationId)
                        }
                    }
                }
                extras.containsKey(FIELD_EVENT) -> {
                    val eventId = extras.getString(FIELD_EVENT)
                    if (eventId != null) presenter.onHandleEvent(eventId)
                }
                extras.containsKey(FIELD_NOTIFICATION) -> {
                    extras.getParcelable<RemoteNotification>(FIELD_NOTIFICATION)?.let {
                        presenter.onHandleNotification(it)
                    }
                }
            }
        }
    }


    override fun showAccountChangeFragment(url: String) {
        findNavController().navigate(
            R.id.change_account_fragment,
            bundleOf("deepLink" to url),
        )
    }

    override fun showProfileSettings() {
        findNavController().navigate(R.id.user_profile_settings_fragment)
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

    override fun showChangePassword(userId: String, code: String) {
        findNavController().navigate(
            R.id.resetPasswordFragment, bundleOf(
                "loginType" to "email",
                "code" to code,
                "userId" to userId
            )
        )
    }

    override fun showPasswordRecovery() {
        val args = RecoveryPasswordFragmentArgs.Builder("").build().toBundle()
        findNavController().navigate(R.id.recovery_password_fragment, args)
    }

    override fun showChat(chatId: String, userName: String) {
        findNavController().navigate(
            R.id.chat_fragment, bundleOf(
                FIELD_NAME to userName,
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
        R.id.welcome_fragment, null,
        navOptions { popUpTo(R.id.main_navigation) { inclusive = true } }
    )

    override fun showLogin() {
        if (!isCurrentDestination(R.id.authorization_fragment)) {
            findNavController().navigate(
                R.id.authorization_fragment, null,
                navOptions { popUpTo(R.id.main_navigation) { inclusive = true } }
            )
        }
    }

    override fun showRecommendations() {
        val args = RecommendationsFragmentArgs.Builder(presenter.isFinishRegister).build().toBundle()
        findNavController().navigate(
            R.id.recommendations_fragment, args,
            navOptions { popUpTo(R.id.main_navigation) { inclusive = true } }
        )
        setFinishRegister(false)
    }

    fun setFinishRegister(isFinish : Boolean) {
        presenter.isFinishRegister = isFinish
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment,
            AboutEventFragmentArgs.Builder(event).build().toBundle()
        )
    }

    override fun showUser(userId: String) {
        findNavController().navigate(
            R.id.user_fragment,
            UserFragmentArgs.Builder(userId).build().toBundle()
        )
    }

    override fun showCurrentUser() {
        findNavController().navigate(R.id.user_profile_fragment)
    }

    override fun showAuthWebsiteFragment(code: String, socketId: String) {
        findNavController().navigate(
            R.id.authWebsiteFragment,
            AuthWebsiteFragmentArgs.Builder(code, socketId).build().toBundle()
        )
    }

    override fun showSupportQuestion(data: SupportData) {
        val args = SupportQuestionDetailFragmentArgs.Builder(data).build().toBundle()
        findNavController().navigate(R.id.supportDetailFragment, args)
    }

    override fun showOrganization(organization: String) {
        findNavController().navigate(
            R.id.organization_fragment_new,
            OrganizationFragmentArgs.Builder(organization).build().toBundle()
        )
    }

    override fun showRating(event: String) {
        findNavController().navigate(
            R.id.event_rating_fragment,
            EventRatingFragmentArgs.Builder(event).build().toBundle()
        )
    }

    override fun showStories() {
        findNavController().navigate(R.id.stories_fragment)
    }

    override fun showUpdateApp(isRequired: Boolean) {
        UpdateAppBottomSheet(this, isRequired)
            .setDismissCallback { presenter.startUpdateTimer(null, isRequired) }
            .show()
        if (isRequired) mBinding.include.inappDim.setBackgroundResource(R.color.main_background)
    }

    override fun showInAppNew(listInApp: List<Notification>) {
        val inAppNotification = InAppNotificationFragment(listInApp)
        inAppNotification.show(supportFragmentManager, "inAppDialog")
    }

    override fun showNoConnectionMessage(show: Boolean) {
        if (show && noInternetDialog?.isShowing != true) {

            BottomSheetDialog(this).apply {
                val dialogBinding = LayoutNoInternetBinding.inflate(layoutInflater)
                dialogBinding.apply {
                    this.btnAction.text = getString(R.string.no_internet_action_retry)
                    this.btnAction.setOnClickListener {
                        presenter.onRetryConnectionClick()
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

    private fun findNavController() = findNavController(R.id.navHostFragment)

    override fun navigateUp() { onSupportNavigateUp() }

    override fun onSupportNavigateUp() = findNavController().navigateUp()

    override fun onDestroy() {
        unregisterFragmentLifecycleCallback()
        super.onDestroy()
    }

    override fun showEmailErrorMessage() {
        ApiErrorDialog(
            this, getString(R.string.email_exist_error_title),
            getString(R.string.email_exist_error_text)
        ).setSelectCallback { }
    }

    override fun showPhoneErrorMessage() {
        ApiErrorDialog(
            this, getString(R.string.phone_exist_error_title),
            getString(R.string.phone_exist_error_text)
        ).setSelectCallback { }
    }

    override fun showErrorMessage(canGoBack: Boolean, message: String) {
    }


    override fun showStateErrorMessage(type: StateType, hasBase: Boolean, user: UserDetail?) {
        ChangeStateBottomDialog(this, type)
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
                                        R.id.maxStatusContactsFragment,
                                        bundleOf("screen" to 1)
                                    )
                                MaxStateScreenType.INTERESTS ->
                                    findNavController().navigate(
                                        R.id.maxStatusInterestsFragment,
                                        bundleOf("screen" to 1)
                                    )
                                MaxStateScreenType.EDUCATION ->
                                    findNavController().navigate(
                                        R.id.maxStatusEducationFragment,
                                        bundleOf("screen" to 1)
                                    )
                                MaxStateScreenType.WORK ->
                                    findNavController().navigate(
                                        R.id.maxStatusWorkFragment,
                                        bundleOf("screen" to 1)
                                    )
                                else -> {}
                            }
                        } else {
                            findNavController().navigate(
                                R.id.mainInfoFragment,
                                bundleOf("type" to UserState.MAX, "screen" to 1)
                            )
                        }
                    }
                }
            }.show()
    }


    override fun setIgnoreTokenListener(isIgnore: Boolean) = presenter.ignoreTokenListener(isIgnore)

    fun connectToSocket() = presenter.connectToSocket()

    private fun setupMainNavBar() {
        mBinding.mainNavBar.setupWithNavController(getNavHostFragment().navController)
        mBinding.mainNavBar.setOnItemReselectedListener { item ->
            val fragment = getNavHostFragment().childFragmentManager.fragments.firstOrNull()
            when (item.itemId) {
                R.id.main -> if (fragment is RecommendationsFragment) fragment.scrollToFirstItem()
                R.id.my_events -> if (fragment is MyEventsFragment) fragment.scrollToFirstItem()
                R.id.notification -> if (fragment is NotificationsListFragment) fragment.scrollToFirstItem()
                R.id.chats -> if (fragment is ChatListTabsFragment) fragment.scrollToFirstItem()
            }
        }
        mBinding.mainNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main -> {
                    if (!findNavController().popBackStack(R.id.recommendations_fragment, false)) {
                        findNavController().navigate(R.id.recommendations_fragment, null,
                            navOptions { popUpTo(R.id.main_navigation) { inclusive = true } })
                    }
                    true
                }
                R.id.my_events -> {
                    if (!findNavController().popBackStack(R.id.my_events_fragment_new, false)) {
                        findNavController().navigate(R.id.my_events_fragment_new)
                    }
                    true
                }
                R.id.chats -> {
                    findNavController().navigate(R.id.chat_list_tabs_fragment)
                    true
                }
                R.id.notification -> {
                    findNavController().navigate(R.id.notifications_list_fragment)
                    true
                }
                R.id.profile -> {
                    findNavController().navigate(R.id.profile_fragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupNavBarItems(f: Fragment) {
        when (f) {
            is RecommendationsFragment -> {
                mBinding.mainNavBar.menu.findItem(R.id.main).isChecked = true
            }
            is ProfileFragment -> {
                mBinding.mainNavBar.menu.findItem(R.id.profile).isChecked = true
            }
            is ChatListTabsFragment -> {
                mBinding.mainNavBar.menu.findItem(R.id.chats).isChecked = true
            }
            is NotificationsListFragment -> {
                mBinding.mainNavBar.menu.findItem(R.id.notification).isChecked = true
            }
            is MyEventsFragment -> {
                mBinding.mainNavBar.menu.findItem(R.id.my_events).isChecked = true
            }
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

    override fun setAppBarElevation(value: Float) {
        mBinding.appBar.changeAppBarElevation(value)
    }

    override fun showBadgeNotification(count: Int) {
        mBinding.mainNavBar.setBadge(R.id.notification, count)
    }

    override fun showBadgeChat(count: Int) {
        mBinding.mainNavBar.setBadge(R.id.chats, count)
    }

    private fun showNavBar() {
        window.navigationBarColor = navBarColorBottomNav
        mBinding.navBarContainer.visibility = View.VISIBLE
    }

    private fun hideNavBar() {
        window.navigationBarColor = navBarColorDefault
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

    override fun showProgressView() = mBinding.progressView.showProgressBar()
    override fun hideProgressView() = mBinding.progressView.hideProgressBar()
    override fun showBrowser(url: String) = showCustomTabsBrowser(this, url)

    private fun registerFragmentLifecycleCallback() {
        getNavHostFragment().childFragmentManager
            .registerFragmentLifecycleCallbacks(navFragmentsLifecycleCallback, false)
    }

    private fun unregisterFragmentLifecycleCallback() {
        getNavHostFragment().childFragmentManager
            .unregisterFragmentLifecycleCallbacks(navFragmentsLifecycleCallback)
    }

    private fun getBehavior(): CustomAppBarLayoutBehavior? {
        val param = mBinding.navHostFragment.layoutParams as CoordinatorLayout.LayoutParams
        if (param.behavior is CustomAppBarLayoutBehavior) {
            return param.behavior as CustomAppBarLayoutBehavior
        } else return null
    }
}
