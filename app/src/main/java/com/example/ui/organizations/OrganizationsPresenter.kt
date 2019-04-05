package com.example.ui.organizations

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Organization
import com.example.extensions.build
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import performOnBackgroundOutOnMain
import withLoadingDialog

@InjectViewState
abstract class OrganizationsPresenter : BasePresenter<OrganizationsContract.View>(), OrganizationsContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0
    protected val pagination = PaginationDataSourceFactory { limit, offset -> loadOrganizations(limit, offset) }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        pagination.build()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.setData(it) }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun attachView(view: OrganizationsContract.View?) {
        super.attachView(view)
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }

    abstract fun loadOrganizations(limit: Int, offset: Int): Maybe<PaginationResponse<Organization>>
}
