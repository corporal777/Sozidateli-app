package com.example.ui.notification.center

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Notification
import com.example.extensions.findItemBy
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.EventRatingDialog
import com.example.util.LayoutListWithPlaceholderUtil
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt


class NotificationsFragment : BaseFragment(), NotificationsContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = getString(R.string.notifications_label)

    @InjectPresenter
    lateinit var presenter: NotificationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<NotificationsPresenter>

    @ProvidePresenter
    fun providePresenter(): NotificationsPresenter = presenterProvider.get()

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

    private val readMoreClickListener: OnNotificationReadMoreClickListener = { presenter.onNotificationReadMoreClick(it) }

    private val readClickListener: OnNotificationReadClickListener = { presenter.onNotificationReadClick(it) }

    private val acceptClickListener: OnNotificationAcceptClickListener = { id, isAccept ->
        presenter.apply {
            if (isAccept) onNotificationAcceptClick(id)
            else onNotificationCancelClick(id)
        }
    }

    private val changeDecisionClickListener: OnNotificationChangeDecisionClickListener = {
        presenter.onNotificationChangeDecisionClick(it)
    }

    private val rateClickListener: OnNotificationRateClickListener = { presenter.onNotificationRateClick(it) }

    private val linkClickListener = BetterLinkMovementMethod.OnLinkClickListener { _, url ->
        presenter.onNotificationUrlClick(url)
        true
    }

    private val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    if (position > 0) presenter.onItemTake(position - 1)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@NotificationsFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
        }

        placeholderUtil = LayoutListWithPlaceholderUtil(view).apply { setDefault() }
    }

    override fun setData(notifications: List<Notification>) {
        adapter.update(notifications.map {
            when (it.type) {
                Notification.Type.SIMPLE -> SimpleNotificationItem(it, readMoreClickListener, linkClickListener, readClickListener)
                Notification.Type.ACCEPTABLE -> AcceptNotificationItem(it, readMoreClickListener, linkClickListener, acceptClickListener, changeDecisionClickListener)
                Notification.Type.RATE -> RateNotificationItem(it, readMoreClickListener, linkClickListener, rateClickListener)
            }
        })
        placeholderUtil.isDataLoad = true
    }

    override fun showNotification(notification: Notification) {
        findNavController().navigate(NotificationsFragmentDirections.notificationsCenterToNotification(notification))
    }

    override fun showUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }

    override fun onNotificationNeedUpdate(id: Int) {
        val idLong = id.toLong()
        adapter.findItemBy { item: Item -> item.id == idLong }?.apply {
            notifyChanged()
        }
    }

    override fun showRatingChooser(id: Int) {
        EventRatingDialog.show(requireContext()) { presenter.onNotificationRatingChosen(id, it.roundToInt()) }
    }


    override fun layout() = R.layout.layout_list_with_placeholder
}
