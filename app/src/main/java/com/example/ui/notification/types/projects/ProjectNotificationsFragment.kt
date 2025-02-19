package com.example.ui.notification.types.projects

import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.example.adapters.notification.NotificationPagerAdapter
import com.example.adapters.notification.NotificationTagsAdapter
import com.example.adapters.notification.NotificationVH
import com.example.app.R
import com.example.data.models.Notification
import com.example.data.models.NotificationLocal
import com.example.extensions.updateItem
import com.example.ui.notification.NotificationType
import com.example.ui.notification.NotificationsSortedData
import com.example.ui.notification.items.*
import com.example.ui.notification.types.base.BaseNotificationTypeFragment
import com.example.ui.notification.types.projects.items.ProjectsTagsItem
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
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

    override var tagsAdapter: NotificationTagsAdapter? = NotificationTagsAdapter(1){
        showProjectInvites(it)
    }

    override fun setNotifications(notifications: PagingData<NotificationLocal>) {
        mBinding.swipeToRefresh.isRefreshing = false
        pagingAdapter.submitData(lifecycle, notifications)
    }

    private fun showProjectInvites(type: ProjectsTagsItem.ProjectsInviteType) {
        if (type == ProjectsTagsItem.ProjectsInviteType.ACTIVE) {
            findNavController().navigate(R.id.active_invites_fragment)
        } else findNavController().navigate(R.id.archive_invites_fragment)
    }

    override val type: NotificationType = NotificationType.PROJECTS
    override val title: CharSequence by lazy { getString(R.string.my_projects) }
}