package com.example.ui.organizations.list

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Organization
import com.example.data.models.Organization.Companion.FIELD_IS_IN_FAVORITE
import com.example.data.models.OrganizationsFilter
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class OrganizationsPresenter
@Inject constructor(
        private val organizationRepository: OrganizationRepository,
        @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<OrganizationsContract.View>(), OrganizationsContract.Presenter {

    lateinit var filter: OrganizationsFilter

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        organizationRepository.getOrganizations(limit, offset, getFilterData())
    }
            .buildList(enablePlaceholders = true)

    private var firstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        when (filter) {
            OrganizationsFilter.FAVORITES -> viewState.setFavoritesHeader()
            OrganizationsFilter.NONE -> viewState.setNoFilterHeader()
        }

        viewState.setOrganizations(List(20) { null })
        compositeDisposable += Observable.create(pagination)
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    if (it.isEmpty()) {
                        when (filter) {
                            OrganizationsFilter.FAVORITES -> viewState.showFavoritesEmptyListPlaceholder()
                            OrganizationsFilter.NONE -> viewState.showNoFilterEmptyListPlaceholder()
                        }
                    } else {
                        viewState.setOrganizations(it)
                    }
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

    override fun attachView(view: OrganizationsContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    override fun onOrganizationClick(organization: Organization) {
        viewState.showOrganization(organization)
    }

    override fun onRemoveFromFavoriteClick(organization: Organization) {
        val request = if (organization.isSubscribed == true) organizationRepository.unsubscribe(organization.id)
        else organizationRepository.subscribe(organization.id)
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { pagination.invalidate() }
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onRefreshRequest() {
        pagination.invalidate()
    }

    override fun onFavoritesClick() {
        viewState.showFavorites()
    }

    private fun getFilterData(): Map<String, Boolean> {
        return when (filter) {
            OrganizationsFilter.FAVORITES -> mapOf(FIELD_IS_IN_FAVORITE to true)
            OrganizationsFilter.NONE -> mapOf()
        }
    }
}
