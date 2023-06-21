package com.example.ui.notification.center.redesign.types.system

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.extensions.updateItem
import com.example.ui.notification.center.redesign.NotificationsSortedData
import com.example.ui.notification.center.redesign.items.*
import com.example.ui.notification.center.redesign.types.base.BaseNotificationTypeFragment
import com.example.ui.state.maxNew.education.MaxStatusEducationPresenter
import com.example.ui.state.maxNew.work.MaxStatusWorkFragmentArgs
import com.xwray.groupie.Section
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


    private val onNotificationListener = object : NotificationItemNew.OnNotificationActionListener {
        override fun onRateClickListener(rateId: String) {}
        override fun onAcceptClickListener(notification: Notification, isAccept: Boolean) {}

        override fun onOpenEventClickListener(eventId: String) = showAboutEvent(eventId)
        override fun onLinkClickListener(url: String) = presenter.onNotificationUrlClick(url)
        override fun onReadClickListener(id: Int) = presenter.onNotificationReadClick(id)
    }

    override fun setNotifications(notifications: List<NotificationsSortedData>) {
        mBinding.swipeToRefresh.isRefreshing = false

//        contentSection.update(notifications.map {
//            NotificationsItemsGroup(
//                requireContext(),
//                it.titleDate,
//                it.data,
//                onNotificationListener
//            )
//        })

        contentSection.update(notifications.map {
            Section().apply {
                if (!it.titleDate.isNullOrEmpty()) add(NotificationsDateItem(it.titleDate))
                add(
                    when (it.data.type) {
                        Notification.Type.SIMPLE -> SimpleNotificationItemNew(
                            requireContext(),
                            it.data,
                            onNotificationListener
                        )
                        Notification.Type.ACCEPTABLE -> AcceptNotificationItemNew(
                            requireContext(),
                            it.data,
                            onNotificationListener
                        )
                        Notification.Type.RATE -> RateNotificationItemNew(
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