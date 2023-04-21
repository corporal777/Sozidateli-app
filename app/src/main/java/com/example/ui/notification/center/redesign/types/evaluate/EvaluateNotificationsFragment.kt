package com.example.ui.notification.center.redesign.types.evaluate

import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.ui.notification.center.redesign.items.NotificationItemNew
import com.example.ui.notification.center.redesign.items.NotificationsItemsGroup
import com.example.ui.notification.center.redesign.types.base.BaseNotificationTypeFragment
import com.example.ui.notification.center.redesign.types.event.EventNotificationsContract
import com.example.ui.notification.center.redesign.types.event.EventNotificationsPresenter
import javax.inject.Inject
import javax.inject.Provider

class EvaluateNotificationsFragment : BaseNotificationTypeFragment<EvaluateNotificationsPresenter>(),
    EvaluateNotificationsContract.View {

    @InjectPresenter
    override lateinit var presenter: EvaluateNotificationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<EvaluateNotificationsPresenter>

    @ProvidePresenter
    fun providePresenter(): EvaluateNotificationsPresenter = presenterProvider.get()


    private val onNotificationListener = object : NotificationItemNew.OnNotificationActionListener {
        override fun onRateClickListener(rateId: String) {}

        override fun onAcceptClickListener(notification: Notification, isAccept: Boolean) {
            presenter.apply {
                if (isAccept) onNotificationAcceptClick(notification)
                else onNotificationCancelClick(notification)
            }
        }
        override fun onOpenEventClickListener(eventId: String) = showAboutEvent(eventId)
        override fun onLinkClickListener(url: String) = presenter.onNotificationUrlClick(url)
        override fun onReadClickListener(id: Int) = presenter.onNotificationReadClick(id)
    }

    override fun setNotifications(notifications: Map<String, List<Notification>>) {
        mBinding.swipeToRefresh.isRefreshing = false

        contentSection.update(notifications.map {
            NotificationsItemsGroup(
                requireContext(),
                it.key,
                it.value,
                onNotificationListener
            )
        })
    }


    override val title: CharSequence by lazy { getString(R.string.estimates_rf) }
}