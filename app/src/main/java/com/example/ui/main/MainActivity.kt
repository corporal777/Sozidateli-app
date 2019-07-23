package com.example.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.OnBackPressedListener
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentActivity
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.ui.views.toolbar.ToolbarContentView
import com.example.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_email_set_social_network.view.btnSave
import kotlinx.android.synthetic.main.dialog_password_recovery.view.*
import javax.inject.Inject
import javax.inject.Provider


class MainActivity : BaseFragmentActivity(), MainContract.View {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>

    @ProvidePresenter
    fun providePresenter(): MainPresenter = presenterProvider.get().apply {
        photoMessageText = getString(R.string.chat_photo_message_text)
    }

    private val startDestinations = arrayOf(
            R.id.event_list_fragment,
            R.id.login_fragment,
            R.id.splash_fragment,
            R.id.event_tabs_fragment
    )

    private val navigatedListener = NavController.OnDestinationChangedListener { controller, destination, arguments ->

    }

    private val navFragmentsLifecycleCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            val destination = findNavController().currentDestination
            supportActionBar?.title = f.arguments?.getString(ARG_CUSTOM_LABEL) ?: destination?.label

            presenter.apply {
                when {
                    destination?.id == R.id.chat_fragment -> presenter.onOpenChatDestination(f.arguments?.getString("chatId", null))
                    isStartDestination(destination?.id) -> onOpenStartDestination()
                    else -> onOpenNotStartDestination()
                }
            }

            if (f is ToolbarFragment) {
                (supportActionBar as? ToolbarContentActionBar)?.apply { f.setupToolbarContent(this) }
                showToolbar()
            } else {
                hideToolbar()
            }
        }
    }

    private var toolbarContentActionBar: ToolbarContentActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setSupportActionBar(toolbar)
        super.getSupportActionBar()?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setCustomView(ToolbarContentView(this@MainActivity), ActionBar.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
        findNavController().addOnDestinationChangedListener(navigatedListener)
        navHostFragment.childFragmentManager.registerFragmentLifecycleCallbacks(navFragmentsLifecycleCallback, false)
        subscribeOnNotificationChanel()
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
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            appLinkData?.also {
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
                    if (snType != null && id != null && authCode != null)
                        presenter.onHandleSocialNetworkConfirm(snType, id, authCode)
                }
            }
        } else {
            val chatData = intent.getBundleExtra(FIELD_CHAT)
            chatData?.let {
                val chatId = it.getString(FIELD_CHAT_ID, null)
                val userName = it.getString(FIELD_LABEL, null)
                val notifiactionId = it.getString(FIELD_NOTIFICATION_ID, null)
                if (chatId != null && userName != null)
                    presenter.onHandleChat(chatId, userName, notifiactionId)
            }
        }
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
        showDialog(getString(R.string.email_change_confirm_success))
    }

    override fun showDialogChangeEmailError() {
        showDialog(getString(R.string.email_change_confirm_error))
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
            .setPopUpTo(R.id.login_fragment, true)
            .build())

    override fun showLogin() = findNavController().navigate(R.id.login_fragment, null, NavOptions.Builder()
            .setPopUpTo(R.id.splash_fragment, true)
            .build())

    override fun showEventList(popUpTo: Int) = findNavController().navigate(R.id.event_list_fragment, null, NavOptions.Builder()
            .setPopUpTo(popUpTo, true)
            .build())

    override fun showEvent() {
        findNavController().apply {
            graph.startDestination = R.id.event_tabs_fragment
            val opts = NavOptions.Builder()
                    .setPopUpTo(R.id.event_list_fragment, true)
                    .build()
            navigate(R.id.event_tabs_fragment, null, opts)
        }
    }

    private fun findNavController() = findNavController(R.id.navHostFragment)

    override fun showToolbar() {
        supportActionBar?.show()
    }

    override fun hideToolbar() {
        supportActionBar?.hide()
    }

    override fun navigateUp() {
        onSupportNavigateUp()
    }

    override fun onSupportNavigateUp() = findNavController().navigateUp()

    override fun onBackPressed() {
        if ((getCurrentFragment() as? OnBackPressedListener)?.onBackPressed() == true) return
        if (findNavController().currentDestination?.id?.let { isStartDestination(it) } == true) {
            finish()
            return
        }

        super.onBackPressed()
    }

    override fun showBackButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    override fun onDestroy() {
        findNavController().removeOnDestinationChangedListener(navigatedListener)
        navHostFragment.childFragmentManager.unregisterFragmentLifecycleCallbacks(navFragmentsLifecycleCallback)
        super.onDestroy()
    }

    private fun isStartDestination(destination: Int?) = startDestinations.contains(destination)

    private fun getCurrentFragment(): Fragment? {
        return navHostFragment.childFragmentManager.primaryNavigationFragment
    }

    override fun getLoadingView(): View = flLoading

    override fun layout() = R.layout.activity_main
}
