package com.example.ui.subscriptions

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Organization
import com.example.data.models.Subscription
import com.example.repository.DummyRepository
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class SubscriptionsPresenter
@Inject constructor(
        private val organizationRepository: OrganizationRepository
) : BasePresenter<SubscriptionsContract.View>(), SubscriptionsContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0
    private val pagination = SimplePagination { limit, offset -> organizationRepository.getOrganizationSubscribers(limit, offset) }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        pagination
                .build()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.apply {
                    setData(it)
                } }, {
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    override fun attachView(view: SubscriptionsContract.View?) {
        super.attachView(view)
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
        pagination.invalidate()
    }

    override fun onUnsubscribeClick(organization: Organization) {
        organizationRepository.unsubscribeOrganization(organization.id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    pagination.invalidate()
                }, {
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }
}
