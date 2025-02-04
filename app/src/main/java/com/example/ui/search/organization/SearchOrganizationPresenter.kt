package com.example.ui.search.organization

import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.EventUserFavorite
import com.example.data.models.Optional
import com.example.data.models.OrganizationNew
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_CITY
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_REGION
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_LIMIT
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_OFFSET
import com.example.data.models.SearchFilter
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.flow.PagingDataSourceFactory
import com.example.util.pagination.flow.applyErrorHandler
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withTimeOut
import javax.inject.Inject

@InjectViewState
class SearchOrganizationPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val organizationRepository: OrganizationRepository
) : SearchPresenter<SearchOrganizationContract.View, SearchFilter.Organization>(appData),
    SearchOrganizationContract.Presenter {

    private var orgFilter = SearchFilter.Organization()

    private val pagination = PagingDataSourceFactory { limit, offset ->
        organizationRepository.searchOrganizations(buildFilterNew(limit, offset))
    }.applyErrorHandler { onReceivePagingError(it) }.buildList(initialSize = SEARCH_PAGE_SIZE, distance = 5)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it) }
            )
    }

    override fun onOrganizationSubscriptionClick(org: OrganizationNew) {
        compositeDisposable += Single.defer {
            if (org.binds?.userFavorite != null)
                eventRepository.deleteFromFavorites(org.binds?.userFavorite?.id.toString())
                    .andThen(Single.just(Optional(null)))
            else eventRepository.addOrgToFavorites(org.id.toString())
                .map { Optional(EventUserFavorite(it.id, it.user)) }
        }
            .doOnSuccess { org.binds?.userFavorite = it.value }
            .withTimeOut(5000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.updateOrganization(org)
                },
                onSuccess = {
                    viewState.apply {
                        updateOrganization(org)
                        if (org.binds?.userFavorite != null) showAddedToFavoriteDialog()
                        else showRemovedFromFavoriteDialog()
                    }
                })
    }

    override fun onOrganizationClick(organization: OrganizationNew) {
        viewState.showOrganization(organization)
    }

    override fun onFiltersApplyClick(filter: SearchFilter.Organization) {
        orgFilter = filter
        viewState.setHasFilter()
        pagination.invalidate()
    }

    override fun onRefreshRequest() = pagination.invalidate()

    override fun isHasFilter(): Boolean = orgFilter.isHasFilter()

    override fun onShowFilterRequest() = viewState.showFilter(orgFilter)

    private fun buildFilterNew(limit: Int, offset: Int): Map<String, Any> =
        mutableMapOf<String, Any>().apply {
            put(ORGANIZATION_LIMIT, limit)
            put(ORGANIZATION_OFFSET, offset)

            if (searchText.isNotEmpty()) put(ORGANIZATION_SEARCH, searchText)

            val name = orgFilter.name
            if (!name.isNullOrEmpty()) put(ORGANIZATION_SEARCH_NAME, "%$name%")

            val inn = orgFilter.inn
            if (!inn.isNullOrEmpty()) {
                if (inn.length > 10) put(ORGANIZATION_SEARCH_OGRN, inn)
                else put(ORGANIZATION_SEARCH_INN, inn)
            }

            put(ORGANIZATION_SEARCH_BINDS, "userFavorite")
            put(ORGANIZATION_SEARCH_TYPE, true)

            //new address filters
            if (!orgFilter.addressRegion.isNullOrEmpty()) {
                put(ORGANIZATION_ADDRESS_REGION, orgFilter.addressRegion!!)
            }
            if (!orgFilter.addressTown.isNullOrEmpty()) {
                put(ORGANIZATION_ADDRESS_CITY, orgFilter.addressTown!!)
            }
            if (!orgFilter.addressTownType.isNullOrEmpty()) {
                put("type", orgFilter.addressTownType!!)
            }
        }

    companion object {
        private const val SEARCH_PAGE_SIZE = 30
        private const val ORGANIZATION_SEARCH_BINDS = "orgBinds"
        private const val ORGANIZATION_SEARCH_INN = "inn"
        private const val ORGANIZATION_SEARCH_OGRN = "ogrn"
        private const val ORGANIZATION_SEARCH_NAME = "orgName"
        private const val ORGANIZATION_SEARCH = "search"
        private const val ORGANIZATION_SEARCH_TYPE = "org"
    }
}