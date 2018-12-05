package com.example.ui.subscriptions

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Subscription
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.BackpressureStrategy
import javax.inject.Inject

@InjectViewState
class SubscriptionsPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<SubscriptionsContract.View>(), SubscriptionsContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val factory = PaginationDataSourceFactory { limit, offset -> dummyRepository.loadSubscriptions(limit, offset) }

        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build()

        RxPagedListBuilder(factory, config)
                .buildFlowable(BackpressureStrategy.LATEST)
                .subscribe({
                    viewState.apply {
                        setData(it)
                    }
                }, {
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    override fun attachView(view: SubscriptionsContract.View?) {
        super.attachView(view)
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

    override fun onSubscriptionClick(subscription: Subscription) {

    }

    override fun onUnsubscribeClick(subscription: Subscription) {

    }

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }
}
