package com.example.ui.notification.types.projects

import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.extensions.updateItem
import com.example.ui.notification.NotificationsSortedData
import com.example.ui.notification.items.*
import com.example.ui.notification.types.base.BaseNotificationTypeFragment
import com.example.ui.notification.types.projects.items.ProjectsTagsItem
import com.xwray.groupie.Section
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


    private val onNotificationListener = object : NotificationItem.OnNotificationActionListener {
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

    override fun setNotifications(notifications: List<NotificationsSortedData>) {
        mBinding.swipeToRefresh.isRefreshing = false

        if (tagsSection.itemCount == 0)
            tagsSection.updateItem(ProjectsTagsItem { showProjectInvites(it) })

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

    private fun showProjectInvites(type: ProjectsTagsItem.ProjectsInviteType) {
        if (type == ProjectsTagsItem.ProjectsInviteType.ACTIVE) {
            findNavController().navigate(R.id.active_invites_fragment)
        } else findNavController().navigate(R.id.archive_invites_fragment)
    }

    override val type: CharSequence by lazy { "project" }
    override val title: CharSequence by lazy { getString(R.string.my_projects) }
}