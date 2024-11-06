package com.example.ui.search.organization

import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.EventUserFavorite
import com.example.data.models.OrganizationNew
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_CITY
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_REGION
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_LIMIT
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_OFFSET
import com.example.data.models.SearchFilter
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class SearchOrganizationPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository
) : SearchPresenter<SearchOrganizationContract.View, SearchFilter.Organization>(appData),
    SearchOrganizationContract.Presenter {

    private var orgFilter = SearchFilter.Organization()

//    override val pagination = PaginationDataSourceFactory { limit, offset ->
//        val data = buildFilterNew(limit, offset)
//        (organizationRepository.searchOrganizations(data) as Maybe<PaginationResponse<Any>>)
//    }

    override fun onOrganizationSubscriptionClick(org: OrganizationNew) {
        compositeDisposable += Completable.defer {
            if (org.binds?.userFavorite != null) {
                eventRepository.deleteFromFavorites(org.binds?.userFavorite?.id.toString())
                    .doOnComplete { org.binds?.userFavorite = null }
            } else {
                eventRepository.addOrgToFavorites(org.id.toString())
                    .doOnSuccess { org.binds?.userFavorite = EventUserFavorite(it.id, it.user) }
                    .ignoreElement()
            }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    changeSubscription(org)
                    if (org.binds?.userFavorite != null) showAddedToFavoriteDialog()
                    else showRemovedFromFavoriteDialog()
                }
            }
    }

    override fun onOrganizationClick(organization: OrganizationNew) {
        viewState.showOrganization(organization)
    }

    override fun onFiltersApplyClick(filter: SearchFilter.Organization) {
        orgFilter = filter
        viewState.setHasFilter()
    }

    override fun onRefreshRequest() {}

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

    private fun addToFavoriteBody(id: Int?): AddToFavoriteModel {
        return AddToFavoriteModel(
            appData.getId(),
            AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_ORGANIZATION, id)
        )
    }

    companion object {
        private const val ORGANIZATION_SEARCH_BINDS = "orgBinds"
        private const val ORGANIZATION_SEARCH_INN = "inn"
        private const val ORGANIZATION_SEARCH_OGRN = "ogrn"
        private const val ORGANIZATION_SEARCH_NAME = "orgName"
        private const val ORGANIZATION_SEARCH = "search"
        private const val ORGANIZATION_SEARCH_TYPE = "org"
    }
}