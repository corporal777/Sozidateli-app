package com.example.ui.notification.center.redesign.types.projects

import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.ui.notification.center.redesign.items.NotificationItemNew
import com.example.ui.notification.center.redesign.items.NotificationsItemsGroup
import com.example.ui.notification.center.redesign.types.base.BaseNotificationTypeFragment
import com.example.ui.notification.center.redesign.types.system.SystemNotificationsContract
import com.example.ui.notification.center.redesign.types.system.SystemNotificationsPresenter
import javax.inject.Inject
import javax.inject.Provider

class ProjectNotificationsFragment : BaseNotificationTypeFragment<ProjectNotificationsPresenter>(),
    ProjectNotificationsContract.View {

    @InjectPresenter
    override lateinit var presenter: ProjectNotificationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<ProjectNotificationsPresenter>

    @ProvidePresenter
    fun providePresenter(): ProjectNotificationsPresenter = presenterProvider.get()


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


    override val title: CharSequence by lazy { getString(R.string.my_projects) }
}