package com.example.ui.search.organization

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.EventUserFavorite
import com.example.data.models.OrganizationNew
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_AREA
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_CITY
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_COUNTRY
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_FEDERAL
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_FLAT
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_HOUSE
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_INDEX
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_REGION
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_SETTLEMENT
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_STREET
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_BINDS
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_LEGAL_INFORMATION_INN
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_LEGAL_INFORMATION_NAME_SHORT
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_LIMIT
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_OFFSET
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class SearchOrganizationPresenter
@Inject constructor(
    private val appData: AppData,
    private val organizationRepository: OrganizationRepository,
    private val eventRepository: EventRepository
) : SearchPresenter<SearchOrganizationContract.View, OrganizationNew, SearchFilter.OrganizationNew>(
    appData
), SearchOrganizationContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        /*
        organizationRepository.searchOrganizations(
            mutableMapOf<String, Any>().apply {
                put(ORGANIZATION_LIMIT, limit)
                put(ORGANIZATION_OFFSET, offset)
                put(ORGANIZATION_BINDS, "userFavorite")

                put("status", "approved")
                put("isSpecial", true)

                if (searchText.isNotEmpty()) put(ORGANIZATION_LEGAL_INFORMATION_NAME_SHORT, "$searchText%")
                val name = filter.name
                if (!name.isNullOrEmpty()) put(ORGANIZATION_LEGAL_INFORMATION_NAME_SHORT, "$name%")
                val inn = filter.inn
                if (!inn.isNullOrEmpty()) put(ORGANIZATION_LEGAL_INFORMATION_INN, inn)
                val address = filter.address
                if (!address.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_STREET, address)
            }

        )
         */

        val data = buildFilterNew(limit, offset)
        organizationRepository.searchOrganizationsNew(data)
    }

    override fun onOrganizationClick(organization: OrganizationNew/*Organization*/) {
        viewState.showOrganization(organization)
    }

    override fun onOrganizationSubscriptionClick(organization: OrganizationNew/*Organization*/) {
        /*val isSubscribed = organization.isSubscribed ?: false
        val request = if (isSubscribed) organizationRepository.unsubscribe(organization.id)
        else organizationRepository.subscribe(organization.id)

        compositeDisposable += request.performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    organization.isSubscribed = !isSubscribed
                    viewState.changeSubscription(organization)
                }*/
        val isSubscribed = organization.binds?.userFavorite != null
        if (isSubscribed) {
            compositeDisposable += eventRepository.deleteFromFavorite(organization.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    organization.binds?.userFavorite = null
                    viewState.changeSubscription(organization)
                }
        } else {
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel(
                    appData.getId(),
                    AddToFavoriteEntityModel(
                        AddToFavoriteEntityModel.FAVORITE_ORGANIZATION,
                        organization.id?.toInt()
                    )
                )
            )
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    organization.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    viewState.changeSubscription(organization)
                }
        }
    }

    private fun buildFilterNew(limit: Int, offset: Int): Map<String, Any> =
        mutableMapOf<String, Any>().apply {
            put(ORGANIZATION_LIMIT, limit)
            put(ORGANIZATION_OFFSET, offset)

            if (searchText.isNotEmpty()) put(ORGANIZATION_SEARCH, "$searchText%")

            val name = filter.name
            if (!name.isNullOrEmpty()) put(ORGANIZATION_SEARCH_NAME, "$name%")

            val inn = filter.inn
            if (!inn.isNullOrEmpty()){
                if (inn.length == 10){
                    put(ORGANIZATION_SEARCH_INN, inn)
                }else if (inn.length == 13){
                    put(ORGANIZATION_SEARCH_OGRN, inn)
                }
            }

            put(ORGANIZATION_SEARCH_BINDS, "userFavorite")
            put(ORGANIZATION_SEARCH_TYPE, true)

            //new address filters
            val index = filter.index
            if (!index.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_INDEX, index)
            val country = filter.country
            if (!country.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_COUNTRY, country)
            val federal = filter.federal
            if (!federal.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_FEDERAL, federal)
            val region = filter.region
            if (!region.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_REGION, region)
            val area = filter.area
            if (!area.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_AREA, area)
            val city = filter.city
            if (!city.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_CITY, city)
            val settlement = filter.settlement
            if (!settlement.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_SETTLEMENT, settlement)
            val street = filter.street
            if (!street.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_STREET, street)
            val house = filter.house
            if (!house.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_HOUSE, house)
            val flat = filter.flat
            if (!flat.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_FLAT, flat)
        }

    override fun createFilter() = SearchFilter.OrganizationNew()
    override fun copyFilter(filter: SearchFilter.OrganizationNew) = filter.copy()

    companion object {
        private const val FILTER_CONTENT = "content"
        private const val FILTER_ADDRESS = "address"
        private const val FILTER_NAME = "name"
        private const val FILTER_INN = "ogrn_inn"
        private const val FILTER_TYPE = "type"
        private const val FILTER_SUBSCRIPTION = "is_subscribed"

        private const val ORGANIZATION_SEARCH_BINDS = "orgBinds"
        private const val ORGANIZATION_SEARCH_INN = "inn"
        private const val ORGANIZATION_SEARCH_OGRN = "ogrn"
        private const val ORGANIZATION_SEARCH_NAME = "orgName"
        private const val ORGANIZATION_SEARCH = "search"
        private const val ORGANIZATION_SEARCH_TYPE = "org"
    }
}