package com.example.ui.subscriptions

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Subscription
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
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
        SimplePagination { limit, offset -> dummyRepository.loadSubscriptions(limit, offset) }
                .build()
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
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
