package com.example.ui.notifications

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import androidx.paging.PagedList
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Notification
import com.example.extensions.formatToDefaultDate
import com.example.ui.base.BaseFragment
import com.example.util.LayoutListWithPlaceholderUtil
import kotlinx.android.synthetic.main.item_notification.*
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import javax.inject.Inject
import javax.inject.Provider


class NotificationsFragment : BaseFragment(), NotificationsContract.View {

    @InjectPresenter
    lateinit var presenter: NotificationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<NotificationsPresenter>

    @ProvidePresenter
    fun providePresenter(): NotificationsPresenter = presenterProvider.get()

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

    private val adapter: SimplePagingRecyclerViewAdapter<Notification> by lazy {
        object : SimplePagingRecyclerViewAdapter<Notification>(
                { oldItem, newItem -> oldItem.id == newItem.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {

            private val linkClickListener = BetterLinkMovementMethod.OnLinkClickListener { _, url ->
                presenter.onNotificationUrlClick(url)
                true
            }

            override fun getItemLayout(itemView: Int) = R.layout.item_notification

            override fun onBindItem(viewHolder: ViewHolder, item: Notification?, position: Int) {
                item!!
                viewHolder.apply {
                    tvMessage.apply {
                        setHtml(item.text)
                        BetterLinkMovementMethod.linkify(Linkify.ALL, this)
                                .setOnLinkClickListener(linkClickListener)
                    }
                    tvDate.text = item.time.formatToDefaultDate() ?: "-"
                }

                presenter.onNotificationOnScreen(item)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@NotificationsFragment.adapter
        }

        placeholderUtil = LayoutListWithPlaceholderUtil(view).apply { setDefault() }
    }

    override fun setData(notifications: PagedList<Notification>) {
        adapter.submitList(notifications)
        placeholderUtil.isDataLoad = true
    }

    override fun showUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }

    override fun layout() = R.layout.layout_list_with_placeholder
}
