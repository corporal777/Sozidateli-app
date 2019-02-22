package com.example.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.App
import com.example.R
import com.example.ui.base.BaseFragmentActivity
import com.example.util.*
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Provider
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Context


class MainActivity : BaseFragmentActivity(), MainContract.View {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>

    @ProvidePresenter
    fun providePresenter(): MainPresenter = presenterProvider.get()

    private val startDestinations = arrayOf(R.id.event_list_fragment, R.id.event_tabs_fragment)

    private val navigatedListener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        supportActionBar?.title = arguments?.getString(ARG_CUSTOM_LABEL) ?: destination.label

        var chatId: String? = null
        if (arguments != null) {
            chatId = arguments.getString("chatId", null)
        }

        if (application is App) {
            (application as App).currentChatID = chatId
        }
        presenter.apply {
            if (startDestinations.contains(destination.id)) onOpenStartDestination()
            else onOpenNotStartDestination()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        val navController = findNavController()
        navController.addOnDestinationChangedListener(navigatedListener)
        subscribeOnNotificationChanel()
        handleIntent(intent)
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
                if (authEmail != null && authCode != null) presenter.onHandleAuthLink(authEmail, authCode)
            }
        }
        val chatData = intent.getBundleExtra(FIELD_CHAT)
        chatData?.let {
            val userId = it.getString(FIELD_SENDER_ID, null)
            val chatId = it.getString(FIELD_CHAT_ID, null)
            val userName = it.getString(FIELD_LABEL, null)
            val notifiactionId = it.getString(FIELD_NOTIFICATION_ID,null)
            if (userId != null && chatId != null && userName != null) presenter.onHandleChat(userId, chatId, userName,notifiactionId)
        }
    }

    override fun showChat(userId: String, chatId: String, userName: String) {
        findNavController().navigate(R.id.chat_fragment, bundleOf(
                FIELD_LABEL to userName,
                FIELD_CHAT_ID to chatId,
                FIELD_USER_ID to userId
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
    }

    private fun findNavController() = findNavController(R.id.navHostFragment)

    override fun hideToolbar() {
        supportActionBar?.hide()
        toolbarDivider.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (!onSupportNavigateUp()) finish()
    }

    override fun navigateUp() {
        onSupportNavigateUp()
    }

    override fun onSupportNavigateUp() = findNavController().navigateUp()

    override fun showToolbar() {
        supportActionBar?.show()
        toolbarDivider.visibility = View.VISIBLE
    }

    override fun showBackButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    override fun onDestroy() {
        findNavController().removeOnDestinationChangedListener(navigatedListener)
        super.onDestroy()
    }

    override fun layout() = R.layout.activity_main
}
