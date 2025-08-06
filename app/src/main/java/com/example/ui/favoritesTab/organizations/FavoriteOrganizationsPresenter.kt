package com.example.ui.favoritesTab.organizations

import com.example.data.AppData
import com.example.data.models.FavoriteModel
import com.example.data.models.OrganizationNew
import com.example.extensions.buildFlow
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
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
    private val eventRepository: EventRepository
) : BasePresenter<FavoriteOrganizationsContract.View>(appData),
    FavoriteOrganizationsContract.Presenter {

    private val pagination = PagingDataSourceFactory { limit, offset ->
        organizationRepository.getFavoriteOrganization(
            mutableMapOf<String, Any>().apply {
                put(FavoriteModel.ORGANIZATION_FAVORITE_LIMIT, limit)
                put(FavoriteModel.ORGANIZATION_FAVORITE_OFFSET, offset)
                put(FavoriteModel.ORGANIZATION_FAVORITE_TYPE, FavoriteModel.ORGANIZATION_TYPE)
                put(FavoriteModel.ORGANIZATION_FAVORITE_LOAD_MODEL, true)
                put(FavoriteModel.ORGANIZATION_FAVORITE_USER, appData.getId())
            }
        )
    }.applyErrorHandler { onReceivePagingError(it) }.buildFlow(initialSize = 30, distance = 5)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it) }
            )
    }


    override fun onRemoveFromFavoriteClick(organization: OrganizationNew) {
        compositeDisposable += eventRepository.deleteFromFavorites(organization.binds?.userFavorite?.id.toString())
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.updateOrganization(organization)
                },
                onComplete = {
                    viewState.showRemovedFromFavoriteDialog()
                    pagination.invalidateStart()
                }
            )
    }

    override fun onOrganizationClick(organization: OrganizationNew) {
        viewState.showOrganization(organization)
    }

    override fun onRefreshRequest() = pagination.invalidate()
}
