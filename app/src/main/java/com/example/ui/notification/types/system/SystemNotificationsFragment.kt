package com.example.ui.notification.types.system

import com.example.app.R
import com.example.data.models.Notification
import com.example.ui.notification.NotificationsSortedData
import com.example.ui.notification.items.*
import com.example.ui.notification.types.base.BaseNotificationTypeFragment
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SystemNotificationsFragment : BaseNotificationTypeFragment<SystemNotificationsPresenter>(),
    SystemNotificationsContract.View {

    @InjectPresenter
    override lateinit var presenter: SystemNotificationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<SystemNotificationsPresenter>

    @ProvidePresenter
    fun providePresenter(): SystemNotificationsPresenter = presenterProvider.get()


    private val onNotificationListener = object : NotificationItem.OnNotificationActionListener {
        override fun onRateClickListener(rateId: String) {}
        override fun onAcceptClickListener(notification: Notification, isAccept: Boolean) {}

        override fun onOpenEventClickListener(eventId: String) = showAboutEvent(eventId)
        override fun onLinkClickListener(url: String) = presenter.onNotificationUrlClick(url)
        override fun onReadClickListener(id: Int) = presenter.onNotificationReadClick(id)
    }

    override fun setNotifications(notifications: List<NotificationsSortedData>) {
        mBinding.swipeToRefresh.isRefreshing = false

        contentSection.update(notifications.map {
            Section().apply {
                if (!it.titleDate.isNullOrEmpty()) add(NotificationsDateItem(it.titleDate))
                add(
                    when (it.data.type) {
                        Notification.Type.SIMPLE -> SimpleNotificationItem(
                            requireContext(),
                            it.data,
                            onNotificationListener
                        )
                        Notification.Type.ACCEPTABLE -> AcceptNotificationItem(
                            requireContext(),
                            it.data,
                            onNotificationListener
                        )
                        Notification.Type.RATE -> RateNotificationItem(
                            requireContext(),
                            it.data,
                            onNotificationListener
                        )
                    }
                )
            }
        })
    }


    override val type: CharSequence by lazy { "system" }
    override val title: CharSequence by lazy { getString(R.string.system_notifications_short) }
}