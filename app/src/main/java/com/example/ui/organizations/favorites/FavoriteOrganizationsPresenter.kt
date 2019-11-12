package com.example.ui.organizations.favorites

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Organization
import com.example.data.models.Organization.Companion.FIELD_IS_IN_FAVORITE
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.applyErrorHandler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.net.UnknownHostException
import javax.inject.Inject

@InjectViewState
class FavoriteOrganizationsPresenter
@Inject constructor(
        private val organizationRepository: OrganizationRepository,
        @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<FavoriteOrganizationsContract.View>(), FavoriteOrganizationsContract.Presenter {

    private val pagination = PaginationDataSourceFactory { limit, offset -> organizationRepository.getOrganizations(limit, offset, mapOf(FIELD_IS_IN_FAVORITE to true)) }
            .applyErrorHandler {
                if (it.cause is UnknownHostException)
                    hasNoConnectionError = true
            }
            .buildList(enablePlaceholders = true)

    private var firstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setOrganizations(List(20) { null })
        compositeDisposable += Observable.create(pagination)
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    viewState.setOrganizations(it)
                }

        compositeDisposable += connectivity
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    if (hasNoConnectionError && it) {
                        hasNoConnectionError = false
                        pagination.invalidate()
                    }
                }
    }

    override fun attachView(view: FavoriteOrganizationsContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    override fun onOrganizationClick(organization: Organization) {
        viewState.showOrganization(organization)
    }

    override fun onRemoveFromFavoriteClick(organization: Organization) {
        compositeDisposable += organizationRepository.unsubscribe(organization.id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ pagination.invalidate() }, { pagination.invalidate() })
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }
}
