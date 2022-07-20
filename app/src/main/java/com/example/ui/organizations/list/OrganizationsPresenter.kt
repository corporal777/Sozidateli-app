package com.example.ui.organizations.list

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.*
import com.example.data.models.Organization.Companion.FIELD_IS_IN_FAVORITE
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.example.ui.views.StateType
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class OrganizationsPresenter
@Inject constructor(
        private val appData: AppData,
        private val organizationRepository: OrganizationRepository,
        private val eventRepository: EventRepository,
        @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<OrganizationsContract.View>(appData), OrganizationsContract.Presenter {

    lateinit var filter: OrganizationsFilter
    private var mDy = 0

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        when (filter) {
            OrganizationsFilter.FAVORITES, OrganizationsFilter.FAVORITES_NO_TITLE -> {
                organizationRepository.getFavoriteOrganization(
                        mutableMapOf<String, Any>().apply {
                            put(FavoriteModel.ORGANIZATION_FAVORITE_LIMIT, limit)
                            put(FavoriteModel.ORGANIZATION_FAVORITE_OFFSET, offset)
                            put(FavoriteModel.ORGANIZATION_FAVORITE_TYPE, FavoriteModel.ORGANIZATION_TYPE)
                            put(FavoriteModel.ORGANIZATION_FAVORITE_LOAD_MODEL, true)
                            put(FavoriteModel.ORGANIZATION_FAVORITE_USER, appData.getId())
                        }
                )
            }
            OrganizationsFilter.NONE -> {
                organizationRepository.searchOrganizations(
                        mutableMapOf<String, Any>().apply {
                            put(OrganizationNew.ORGANIZATION_LIMIT, limit)
                            put(OrganizationNew.ORGANIZATION_OFFSET, offset)
                            put(OrganizationNew.ORGANIZATION_BINDS, "userFavorite")
                        }
                )
            }
        }
    }.buildList(enablePlaceholders = true)

    private var firstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(0f)
        when (filter) {
            OrganizationsFilter.FAVORITES -> viewState.setFavoritesHeader()
            OrganizationsFilter.NONE -> viewState.setNoFilterHeader()
            OrganizationsFilter.FAVORITES_NO_TITLE -> {
                // do nothing
            }
        }

        viewState.setOrganizations(List(20) { null })
        compositeDisposable += Observable.create(pagination)
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    if (it.isEmpty()) {
                        when (filter) {
                            OrganizationsFilter.FAVORITES_NO_TITLE,
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
        viewState.setAppBarElevation(Math.abs(mDy / 10f))
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    override fun changeAppBarElevation(value: Int) {
        mDy += value
        viewState.setAppBarElevation(Math.abs(mDy / 10f))
    }

    override fun onOrganizationClick(organization: OrganizationNew/*Organization*/) {
        viewState.showOrganization(organization)
    }

    override fun onRemoveFromFavoriteClick(organization: OrganizationNew/*Organization*/) {
        /*val request = if (organization.isSubscribed == true) organizationRepository.unsubscribe(organization.id)
        else organizationRepository.subscribe(organization.id)
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { pagination.invalidate() }*/
        val isSubscribed = organization.binds?.userFavorite != null
        if (isSubscribed) {
            compositeDisposable += eventRepository.deleteFromFavorite(organization.binds?.userFavorite?.id.toString())
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        organization.binds?.userFavorite = null
                        viewState.changeSubscription(organization)
                        //pagination.invalidate()
                    }
        } else {
            compositeDisposable += eventRepository.addToFavorites(AddToFavoriteModel(appData.getId(), AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_ORGANIZATION, organization.id?.toInt())))
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        organization.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                        viewState.changeSubscription(organization)
                        //pagination.invalidate()
                    }
        }
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
            OrganizationsFilter.FAVORITES_NO_TITLE,
            OrganizationsFilter.FAVORITES -> mapOf(FIELD_IS_IN_FAVORITE to true)
            OrganizationsFilter.NONE -> mapOf()
        }
    }
}
