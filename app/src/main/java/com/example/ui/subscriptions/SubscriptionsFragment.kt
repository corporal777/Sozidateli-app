package com.example.ui.subscriptions

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Subscription
import com.example.ui.base.BaseFragment
import com.example.util.CropCircleTransformation
import com.example.util.PositionOffsetScrollListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_events_list.*
import kotlinx.android.synthetic.main.item_subscription.*
import javax.inject.Inject
import javax.inject.Provider

class SubscriptionsFragment : BaseFragment(), SubscriptionsContract.View {

    @InjectPresenter(type = PresenterType.WEAK, tag = "SubscriptionsPresenter")
    lateinit var presenter: SubscriptionsPresenter

    @Inject
    lateinit var presenterProvider: Provider<SubscriptionsPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = "SubscriptionsPresenter")
    fun providePresenter(): SubscriptionsPresenter = presenterProvider.get()

    private val adapter: SimplePagingRecyclerViewAdapter<Subscription> by lazy {
        object : SimplePagingRecyclerViewAdapter<Subscription>(
                { oldItem, newItem -> oldItem.id == newItem.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_subscription

            override fun onBindItem(viewHolder: ViewHolder, item: Subscription?, position: Int) {
                item!!
                viewHolder.apply {
                    Picasso.get()
                            .load(item.logo)
                            .transform(CropCircleTransformation())
                            .placeholder(R.drawable.ic_launcher)
                            .into(ivLogo)

                    tvLabel.text = item.name
                    itemView.setOnClickListener { presenter.onSubscriptionClick(item) }
                    btnUnsubscribe.setOnClickListener { presenter.onUnsubscribeClick(item) }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@SubscriptionsFragment.adapter
            if (itemDecorationCount == 0) {
                addItemDecoration(DividerItemDecoration(context, VERTICAL))
            }
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })
        }
    }

    override fun setData(data: PagedList<Subscription>) {
        adapter.submitList(data)
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_events_list
}
