package com.example.ui.notifications

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Notification
import com.example.ui.base.BaseFragment
import com.example.util.Utils
import kotlinx.android.synthetic.main.fragment_news_list.*
import kotlinx.android.synthetic.main.item_notification.*
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
                viewHolder.apply {
                    tvMessage.apply {
                        setHtml(item?.text ?: "-")
                        BetterLinkMovementMethod.linkify(Linkify.ALL, this)
                                .setOnLinkClickListener(linkClickListener)
                    }
                    tvDate.text = item?.time?.let { Utils.formatToDefaultDate(it) } ?: "-"
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@NotificationsFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, VERTICAL))
        }
    }

    override fun setData(notifications: PagedList<Notification>) {
        adapter.submitList(notifications)
    }

    override fun showUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_notifications
}
