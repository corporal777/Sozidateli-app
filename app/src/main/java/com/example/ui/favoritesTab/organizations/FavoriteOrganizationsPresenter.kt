package com.example.ui.favoritesTab.organizations

import com.example.data.AppData
import com.example.data.models.EventUserFavorite
import com.example.data.models.FavoriteModel
import com.example.data.models.OrganizationNew
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class FavoriteOrganizationsPresenter
@Inject constructor(
        private val appData: AppData,
        private val organizationRepository: OrganizationRepository,
        private val eventRepository: EventRepository,
        @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<FavoriteOrganizationsContract.View>(appData),
    FavoriteOrganizationsContract.Presenter {

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        organizationRepository.getFavoriteOrganization(
            mutableMapOf<String, Any>().apply {
                put(FavoriteModel.ORGANIZATION_FAVORITE_LIMIT, limit)
                put(FavoriteModel.ORGANIZATION_FAVORITE_OFFSET, offset)
                put(FavoriteModel.ORGANIZATION_FAVORITE_TYPE, FavoriteModel.ORGANIZATION_TYPE)
                put(FavoriteModel.ORGANIZATION_FAVORITE_LOAD_MODEL, true)
                put(FavoriteModel.ORGANIZATION_FAVORITE_USER, appData.getId())
            }
        )
    }.buildList(enablePlaceholders = false, initialSize = 30)

    private var firstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setOrganizations(List(20) { null })
        compositeDisposable += Observable.create(pagination)
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    if (it.isEmpty()) viewState.showFavoritesEmptyListPlaceholder()
                    else viewState.setOrganizations(it)
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

    override fun onRemoveFromFavoriteClick(organization: OrganizationNew) {
        compositeDisposable += Completable.defer {
            if (organization.binds?.userFavorite != null) {
                eventRepository.deleteFromFavorites(organization.binds?.userFavorite?.id.toString())
                    .doOnComplete { organization.binds?.userFavorite = null }
            } else {
                eventRepository.addOrgToFavorites(organization.id.toString())
                    .doOnSuccess {
                        organization.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    }.ignoreElement()
            }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    changeSubscription(organization)
                    if (organization.binds?.userFavorite != null) showAddedToFavoriteDialog()
                    else showRemovedFromFavoriteDialog()
                }
            }
    }

    override fun onOrganizationClick(organization: OrganizationNew) = viewState.showOrganization(organization)
    override fun onItemTake(position: Int) = pagination.onItemTake(position)
    override fun onRefreshRequest() = pagination.invalidate()
}
