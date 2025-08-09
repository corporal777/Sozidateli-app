package com.example.ui.main


//class MainActivity : BaseFragmentActivity(), MainContract.View {
//
//    @InjectPresenter
//    lateinit var presenter: MainPresenter
//
//    @Inject
//    lateinit var presenterProvider: Provider<MainPresenter>
//
//    @ProvidePresenter
//    fun providePresenter(): MainPresenter = presenterProvider.get()
//
//    private val navBarColorDefault by lazy { getColor(R.color.main_background) }
//    private val navBarColorBottomNav by lazy { getColor(R.color.bottom_navigation_view_background_color) }
//
//    private var noInternetDialog: BottomSheetDialog? = null
//    private lateinit var splashScreen: SplashScreen
//
//    private val navFragmentsLifecycleCallback = getFragmentLifecycleCallback(
//        onFragmentStopped = { },
//        onFragmentDestroyed = { },
//        onFragmentStarted = { f -> setupBackgroundTransparency(f) },
//        onBottomSheetViewCreated = { },
//        onFragmentViewCreated = { f ->
//            presenter.onOpenCheckConnectionDestination(f is DoNotCheckConnectionFragment)
//
//            setupNavBar(f)
//            setupNavBarItems(f)
//            setupBackgroundImageFragment(f)
//
//            mBinding.appBar.isVisible = f is ToolbarFragment
//            if (f is ToolbarFragment) {
//                mBinding.toolbar.apply {
//                    toolbarLabel.text = f.title
//                    f.setupToolbarContent(ToolbarContent(ivBack, toolbarLabel, toolbarContainer))
//                    f.actionIconContainer(toolbarContainer)
//                }
//            }
//        }
//    )
//
//    private val backClick = onBackPressedCallback(true) {
//
//    }
//
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        showSplashScreen()
//        super.onCreate(savedInstanceState)
//
//        onBackPressedDispatcher.addCallback(this, backClick)
//        registerFragmentLifecycleCallback()
//        setupMainNavBar()
//        subscribeOnNotificationChanel()
//
//        mBinding.toolbar.ivBack.setOnClickListener {
//            val fragment = getNavHostFragment().childFragmentManager.fragments.firstOrNull()
//            if (fragment != null && fragment is BaseFragment<*>) fragment.navigateUp()
//            else if (fragment != null && fragment is BaseVBFragment<*>) fragment.navigateUp()
//            else navigateUp()
//        }
//    }
//
//
//
//    override fun showSplashScreen() {
//        splashScreen = installSplashScreen().apply { setKeepOnScreenCondition { true } }
//    }
//
//    override fun hideSplashScreen() {
//        splashScreen.setKeepOnScreenCondition { false }
//    }
//
//    private fun wasLaunchedFromResents(intent: Intent): Boolean {
//        val fromHistory = Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
//        return intent.flags and fromHistory == fromHistory
//    }
//
//    override fun checkIntent() = handleIntent(intent)
//    override fun clearIntentData() = intent.let { it.data = null }
//
//    override fun handleIntent(intent: Intent) {
//        if (wasLaunchedFromResents(intent)) return
//        if (Intent.ACTION_VIEW == intent.action) {
//            intent.data?.also {
//                val authCode = it.getQueryParameter(AUTH_CONFIRM_EMAIL_CODE)
//                val paths = it.pathSegments
//                val lastPath = it.lastPathSegment
//
//                //catch path auth
//                if (lastPath == PATH_AUTH || lastPath == PATH_SWITCH_ACCOUNT) {
//                    val redirectLink = it.getQueryParameter("redirect")
//                    presenter.onHandleAuthToOtherPlatform(redirectLink)
//                }
//                //catch path qr code
//                else if (lastPath == PATH_QR) {
//                    val code = it.getQueryParameter(AUTH_CONFIRM_EMAIL_CODE)
//                    val id = it.getQueryParameter(AUTH_CONFIRM_SOCKET_ID)
//                    presenter.onHandleAuthWebsite(code, id)
//                }
//                //catch path event
//                else if (!lastPath.isNullOrEmpty() && paths.contains(PATH_EVENT)) {
//                    if (lastPath.contains(PATH_HIDDEN)) presenter.onHandleEventCode(authCode)
//                    else presenter.onHandleEvent(lastPath)
//                }
//                //catch path profile settings
//                else if (lastPath == PATH_SETTINGS) presenter.onHandleProfileSettings()
//                //catch path profile
//                else if (lastPath == PATH_PROFILE) presenter.onHandleProfile()
//                //catch path user
//                else if (paths.contains(PATH_USER) && !lastPath.isNullOrBlank()) {
//                    presenter.onHandleUser(lastPath)
//                }
//                //catch path support center question
//                else if (lastPath == PATH_SUPPORT_CENTER) {
//                    val question = it.getQueryParameter("question")
//                    presenter.onHandleSupportQuestion(question)
//                }
//                //catch path recover password
//                else if (lastPath == PATH_LP) {
//                    if (it.fragment == "recover-password") presenter.onHandleRecoverPassword()
//                }
//                //catch path password change
//                else if (lastPath == PASSWORD_RECOVERY && authCode != null) {
//                    val indexLastPath = paths.indexOf(lastPath)
//                    val userId = if (indexLastPath > 0) paths[indexLastPath - 1] else ""
//                    presenter.onHandleChangePassword(userId, authCode)
//                }
//                //catch path event member
//                else if (lastPath == PATH_EVENT_MEMBER) {
//                    val memberEmail = it.getQueryParameter(AUTH_CONFIRM_EMAIL) ?: ""
//                    val memberCode = it.getQueryParameter(AUTH_CONFIRM_EMAIL_CODE) ?: ""
//                    presenter.onInviteRegister(memberEmail, memberCode, "", "", "", -1)
//                }
//                //catch path pgrf, assistant
//                else if (lastPath == PGRF || lastPath == ASSISTANT || lastPath == LINKED_REGISTER) {
//                    val json = decodeBase64ToJson(it.getQueryParameter("data")) ?: return
//                    val invite = it.getQueryParameter(AUTH_CONFIRM_INVITE_ID)
//                    presenter.onInviteRegister(
//                        if (json["email"].toString() != "null") json["email"].toString() else "",
//                        authCode ?: "",
//                        if (json["name"].toString() != "null") json["name"].toString() else "",
//                        if (json["lastName"].toString() != "null") json["lastName"].toString() else "",
//                        if (json["middleName"].toString() != "null") json["middleName"].toString() else "",
//                        invite?.toInt() ?: -1
//                    )
//                }
//            }
//        } else {
//            val extras = intent.extras ?: return
//            when {
//                extras.containsKey(FIELD_CHAT) -> {
//                    val bundle = intent.getBundleExtra(FIELD_CHAT) ?: return
//                    val chatId = bundle.getString(FIELD_CHAT_ID, null)
//                    val userName = bundle.getString(FIELD_LABEL, null)
//                    val notificationId = bundle.getString(FIELD_NOTIFICATION_ID, null)
//                    if (chatId != null && userName != null && notificationId != null)
//                        presenter.onHandleChat(chatId, userName, notificationId)
//                }
//
//                extras.containsKey(FIELD_EVENT) -> {
//                    val eventId = extras.getString(FIELD_EVENT)
//                    if (eventId != null) presenter.onHandleEvent(eventId)
//                }
//
//                extras.containsKey(FIELD_NOTIFICATION) -> {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                        extras.getParcelable(FIELD_NOTIFICATION, RemoteNotification::class.java)
//                            ?.let {
//                                presenter.onHandleNotification(it)
//                            }
//                    else extras.getParcelable<RemoteNotification>(FIELD_NOTIFICATION)?.let {
//                        presenter.onHandleNotification(it)
//                    }
//                }
//            }
//        }
//    }
//
//
//    override fun showAccountChangeFragment(url: String) {
//    }
//
//    override fun showProfileSettings() {
//    }
//
//    override fun showInviteRegister(
//        email: String,
//        code: String,
//        name: String,
//        lastName: String,
//        middleName: String,
//        invite: Int
//    ) {
//
//    }
//
//    override fun showChangePassword(userId: String, code: String) {
//
//    }
//
//    override fun showPasswordRecovery() {
//
//    }
//
//    override fun showChat(chatId: String, userName: String) {
//
//    }
//
//    private fun subscribeOnNotificationChanel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Create channel to show notifications.
//            val channelId = getString(R.string.app_name)
//            val notificationManager = getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(
//                NotificationChannel(
//                    channelId,
//                    channelId, NotificationManager.IMPORTANCE_HIGH
//                )
//            )
//        }
//    }
//
//    override fun showGreetings(){
//
//    }
//
//
//    override fun showLogin() {
//
//    }
//
//    override fun showRecommendations() {
//
//    }
//
//    fun setFinishRegister(isFinish: Boolean) {
//        presenter.isFinishRegister = isFinish
//    }
//
//    override fun showAboutEvent(event: String) {
//
//    }
//
//    override fun showUser(userId: String) {
//
//    }
//
//    override fun showCurrentUser() {
//
//    }
//
//    override fun showAuthWebsiteFragment(code: String, socketId: String) {
//
//    }
//
//    override fun showSupportQuestion(data: SupportData) {
//
//    }
//
//    override fun showOrganization(organization: String) {
//
//    }
//
//    override fun showStories() {
//
//    }
//
//    override fun showUpdateApp(isRequired: Boolean) {
//        UpdateAppBottomSheetDialog(this, isRequired)
//            .setDismissCallback { presenter.startUpdateTimer(null, isRequired) }
//            .show()
//    }
//
//    override fun showInAppNew(listInApp: List<Notification>) {
//        InAppNotificationFragment()
//            .setArgument<InAppNotificationFragment>(IN_APP_FRAGMENT_TAG, listInApp)
//            .show(supportFragmentManager)
//    }
//
//
//    override fun showNoConnectionMessage(show: Boolean) {
//        if (show && noInternetDialog?.isShowing != true) {
//
//            BottomSheetDialog(this).apply {
//                val dialogBinding = LayoutNoInternetBinding.inflate(layoutInflater)
//                dialogBinding.apply {
//                    this.btnAction.text = getString(R.string.no_internet_action_retry)
//                    this.btnAction.setOnClickListener {
//                        presenter.onRetryConnectionClick()
//                    }
//                }
//                setContentView(dialogBinding.root)
//                setCancelable(false)
//                setOnKeyListener { _, keyCode, _ ->
//                    if (keyCode == KeyEvent.KEYCODE_BACK) finish()
//                    true
//                }
//                noInternetDialog = this
//            }.show()
//        } else if (!show) {
//            noInternetDialog?.dismiss()
//        }
//    }
//
//    private fun findNavController() {
//
//    }
//
//
//    override fun navigateUp() {
//        onSupportNavigateUp()
//    }
//
//
//
//    override fun showEventRegistrationSuccessDialog() {
//        EventRegistrationSuccessBottomDialog(this)
//            .setSelectCallback { }
//            .show()
//    }
//
//    override fun showEmailErrorMessage() {
//        DefaultAlertDialog(
//            this,
//            getString(R.string.email_exist_error_title),
//            getString(R.string.email_exist_error_text)
//        )
//    }
//
//    override fun showPhoneErrorMessage() {
//        DefaultAlertDialog(
//            this,
//            getString(R.string.phone_exist_error_title),
//            getString(R.string.phone_exist_error_text)
//        )
//    }
//
//    override fun showErrorMessage(canGoBack: Boolean, message: String) {
//    }
//
//
//    override fun showStateErrorMessage(type: StateType, hasBase: Boolean, user: UserDetail?) {
//
//        ChangeStateBottomDialog(this, type)
//            .setClickCallback {
//                when (it) {
//                    ClickType.INFO -> { }
//                    ClickType.BASE -> {
//
//                    }
//                    ClickType.MAX -> {
//
//                    }
//                }
//            }.show()
//    }
//
//
//    override fun setIgnoreTokenListener(isIgnore: Boolean) = presenter.ignoreTokenListener(isIgnore)
//
//    fun connectToSocket() = presenter.connectToSocket()
//
//    private fun setupMainNavBar() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
//            setSystemBarsAppearance(SYSTEM_UI_LIGHT_NAV_BAR)
//
//        mBinding.mainNavBar.setupWithNavController(getNavHostFragment().navController)
//        mBinding.mainNavBar.setOnItemReselectedListener { item ->
//            val fragment = getNavHostFragment().childFragmentManager.fragments.firstOrNull()
//
//        }
//
//    }
//
//    private fun setupNavBar(f: Fragment) {
//
//    }
//
//    private fun setupNavBarItems(f: Fragment) {
//
//    }
//
//
//    override fun showRequestErrorMessage() {
//        DefaultAlertDialog(this, null, getString(R.string.request_server_error))
//    }
//
//    override fun showSnackBar(@StringRes message: Int) = showSnackBar(getString(message))
//    override fun showSnackBar(message: String) = showErrorMessage(message)
//
//    override fun showErrorMessage(message: String) {
//        mBinding.viewSnackBar.setText(message)
//        mBinding.viewSnackBar.show(mBinding.root)
//        presenter.onRequestShowErrorMessage()
//    }
//    override fun hideErrorMessage() = mBinding.viewSnackBar.hide(mBinding.root)
//
//    override fun setAppBarElevation(value: Float) {
//        mBinding.appBar.elevation = if (value <= 10f) value else 10f
//    }
//
//    override fun showBadgeNotification(count: Int) = mBinding.mainNavBar.setBadge(R.id.notification, count)
//    override fun showBadgeChat(count: Int) = mBinding.mainNavBar.setBadge(R.id.chats, count)
//
//    private fun showNavBar() {
//        window.navigationBarColor = navBarColorBottomNav
//        mBinding.mainNavBar.visibility = View.VISIBLE
//    }
//
//    private fun hideNavBar() {
//        window.navigationBarColor = navBarColorDefault
//        mBinding.mainNavBar.visibility = View.GONE
//    }
//
//    override fun showProgressView() = mBinding.progressView.showProgressBar()
//    override fun hideProgressView() = mBinding.progressView.hideProgressBar()
//    override fun showBrowser(url: String) = showCustomTabsBrowser(this, url)
//
//
//    private fun registerFragmentLifecycleCallback() {
//        getNavHostFragment().childFragmentManager
//            .registerFragmentLifecycleCallbacks(navFragmentsLifecycleCallback, false)
//    }
//
//    private fun unregisterFragmentLifecycleCallback() {
//        getNavHostFragment().childFragmentManager
//            .unregisterFragmentLifecycleCallbacks(navFragmentsLifecycleCallback)
//    }
//
//    override fun onDestroy() {
//        unregisterFragmentLifecycleCallback()
//        super.onDestroy()
//    }
//}
