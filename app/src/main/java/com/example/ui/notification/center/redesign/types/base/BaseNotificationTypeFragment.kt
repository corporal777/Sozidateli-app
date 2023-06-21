package com.example.ui.notification.center.redesign.types.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.Notification
import com.example.databinding.FragmentNotificationsTypesBinding
import com.example.extensions.findGroupBy
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.AboutEventFragmentNewArgs
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.notification.center.redesign.NotificationType
import com.example.ui.notification.center.redesign.invites.InviteNotificationsBottomSheet
import com.example.ui.notification.center.redesign.items.NotificationItemNew
import com.example.ui.notification.center.redesign.items.NotificationsItemsGroup
import com.example.ui.views.LinearLayoutManagerAccurateOffset
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.showCustomTabsBrowser
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled


abstract class BaseNotificationTypeFragment<P : BaseNotificationTypeContract.Presenter> :
    BaseFragmentNew<FragmentNotificationsTypesBinding>(),
    BaseNotificationTypeContract.View {

    abstract var presenter: P

    val contentSection by lazy { Section() }
    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(contentSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    val datesCount = presenter.getTitleDatesCount()
                    if (position < datesCount) return
                    presenter.onItemTake(position - datesCount)

                    //if (position > 0) presenter.onItemTake(position - 1)
                }
            })
        }
    }

    private val mLayoutManager by lazy { LinearLayoutManager(requireContext()) }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            notificationsList.apply {
                adapter = groupAdapter
                layoutManager = mLayoutManager
                onScrolled { dx, dy ->
                    presenter.changeScrollingElevation(this.computeVerticalScrollOffset())
                }
            }
            btnReadAll.apply {
                setOnClickListener { presenter.onReadAllClick() }
            }
            toolbarLabel.apply {
                text = title
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setPlaceholder(notifications: List<Notification?>) {
        contentSection.update(notifications.map {
            PlaceholderItem(PlaceholderItem.Type.NOTIFICATIONS_LIST)
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showEmptyListPlaceholder() {
        contentSection.updateItem(NoEventItem(getString(R.string.notifications_not_found)))
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun onNotificationNeedUpdate(data: Notification) {
//        val date = data.date?.split(" ")?.get(0)
//        val group = contentSection.findGroupBy<NotificationsItemsGroup> { x -> x.isSameDate(date) }
//        if (group != null){
//            group.updateNotification(data)
//        }

        val idLong = data.id.toLong()
        val item = contentSection.findItemBy<NotificationItemNew<*>> { x -> x.id == idLong }
        if (item != null) item.notifyChanged(data)
    }

    override fun showAboutEvent(eventId: String?) {
        if (!eventId.isNullOrEmpty()){
            findNavController().navigate(
                R.id.about_event_fragment_new,
                AboutEventFragmentNewArgs.Builder(eventId).build().toBundle()
            )
        }
    }

    override fun showAboutOrganization(organizationId: String?) {
        if (!organizationId.isNullOrEmpty()){
            findNavController().navigate(
                R.id.organization_fragment_new,
                bundleOf("organizationId" to organizationId)
            )
        }
    }

    override fun showInvitesBottomSheet(type: NotificationType) {
        val inviteNotifications = InviteNotificationsBottomSheet(type)
        inviteNotifications.show(requireActivity().supportFragmentManager, "invitesDialog")
    }

    override fun showUrl(url: String) = showCustomTabsBrowser(requireContext(), url)

    override fun setReadAllButton(show: Boolean) {
        mBinding.apply {
            btnReadAll.isVisible = show
            toolbarLabel.updateLayoutParams<ConstraintLayout.LayoutParams> {
                if (btnReadAll.isVisible) {
                    endToStart = btnReadAll.id
                    horizontalBias = 0.764F
                }
                else {
                    endToStart = guidelineRight.id
                    horizontalBias = 0.5F
                }
                toolbarLabel.isVisible = true
            }
        }
    }

    override fun setAppBarElevation(shadow: Float) {
        mBinding.appBar.changeAppBarElevation(shadow)
    }

    abstract val title: CharSequence
    abstract val type : CharSequence

    override fun layout(): Int = R.layout.fragment_notifications_types
}