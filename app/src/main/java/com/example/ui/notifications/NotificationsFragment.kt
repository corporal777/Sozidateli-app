package com.example.ui.notifications

import androidx.paging.PagedList
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Notification
import com.example.ui.base.BaseFragment
import com.example.util.DATE_FORMAT_SHORT_MONTH_FULL_YEAR
import kotlinx.android.synthetic.main.fragment_news_list.*
import kotlinx.android.synthetic.main.item_notification.*
import java.text.SimpleDateFormat
import java.util.*
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

            private val dateFormat by lazy {
                SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault())
            }

            override fun getItemLayout(itemView: Int) = R.layout.item_notification

            override fun onBindItem(viewHolder: ViewHolder, item: Notification?, position: Int) {
                item!!
                viewHolder.apply {
                    tvMessage.text = item.text
                    tvDate.text = item.time
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

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_notifications
}
