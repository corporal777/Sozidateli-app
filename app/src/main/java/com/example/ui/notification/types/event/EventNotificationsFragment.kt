package com.example.ui.notification.types.event

import androidx.paging.PagingData
import com.example.adapters.notification.NotificationPagerAdapter
import com.example.adapters.notification.NotificationVH
import com.example.app.R
import com.example.data.models.Notification
import com.example.data.models.NotificationLocal
import com.example.ui.notification.NotificationType
import com.example.ui.notification.items.*
import com.example.ui.notification.types.base.BaseNotificationTypeFragment
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class EventNotificationsFragment : BaseNotificationTypeFragment<EventNotificationsPresenter>(),
    EventNotificationsContract.View {

    @InjectPresenter
    override lateinit var presenter: EventNotificationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventNotificationsPresenter>

    @ProvidePresenter
    fun providePresenter(): EventNotificationsPresenter = presenterProvider.get()



    private val onNotificationListener = object : NotificationVH.OnNotificationActionListener {
        override fun onReadClick(id: Int) = presenter.onNotificationReadClick(id)
        override fun onLinkClick(url: String) = presenter.onNotificationUrlClick(url)
        override fun onOpenEventClick(eventId: String) = showAboutEvent(eventId)
        override fun onAcceptClick(notification: NotificationLocal, isAccept: Boolean) {
            presenter.apply {
                if (isAccept) onNotificationAcceptClick(notification)
                else onNotificationCancelClick(notification)
            }
        }
        override fun onRateClick(rateId: String) {}
    }

    override val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        NotificationPagerAdapter(onNotificationListener)
    }

    override fun setNotifications(notifications: PagingData<NotificationLocal>) {
        mBinding.swipeToRefresh.isRefreshing = false
        pagingAdapter.submitData(lifecycle, notifications)
    }


    override val type: NotificationType = NotificationType.EVENTS
    override val title: CharSequence by lazy { getString(R.string.events) }
}