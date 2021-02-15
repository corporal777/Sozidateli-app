package com.example.ui.search.organization

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Organization
import com.example.data.models.OrganizationNew
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_ADDRESS_STREET
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_LEGAL_INFORMATION_INN
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_LEGAL_INFORMATION_NAME_FULL
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_LEGAL_INFORMATION_NAME_SHORT
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_LIMIT
import com.example.data.models.OrganizationNew.Companion.ORGANIZATION_OFFSET
import com.example.data.models.SearchFilter
import com.example.repository.OrganizationRepository
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class SearchOrganizationPresenter
@Inject constructor(
        private val organizationRepository: OrganizationRepository
) : SearchPresenter<SearchOrganizationContract.View, OrganizationNew, SearchFilter.OrganizationNew>(), SearchOrganizationContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        //organizationRepository.getOrganizations(limit, offset, buildFilter())
        organizationRepository.searchOrganizations(
                mutableMapOf<String, Any>().apply {
                    put(ORGANIZATION_LIMIT, limit)
                    put(ORGANIZATION_OFFSET, offset)
                    if (searchText.isNotEmpty()) put(ORGANIZATION_LEGAL_INFORMATION_NAME_SHORT, "$searchText%")
                    val name = filter.name
                    if (!name.isNullOrEmpty()) put(ORGANIZATION_LEGAL_INFORMATION_NAME_SHORT, "$name%")
                    val inn = filter.inn
                    if (!inn.isNullOrEmpty()) put(ORGANIZATION_LEGAL_INFORMATION_INN, inn)
                    val address = filter.address
                    if (!address.isNullOrEmpty()) put(ORGANIZATION_ADDRESS_STREET, address)
                }
        )
    }

    override fun onOrganizationClick(organization: OrganizationNew) {
        viewState.showOrganization(organization)
    }

    override fun onOrganizationSubscriptionClick(organization: OrganizationNew) {
        //TODO not ready on api side
        /*val isSubscribed = organization.isSubscribed ?: false
        val request = if (isSubscribed) organizationRepository.unsubscribe(organization.id)
        else organizationRepository.subscribe(organization.id)

        compositeDisposable += request.performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    organization.isSubscribed = !isSubscribed
                    viewState.changeSubscription(organization)
                }*/
    }

    private fun buildFilter(): Map<String, Any> = mutableMapOf<String, Any>().apply {
        if (searchText.isNotEmpty()) put(FILTER_CONTENT, searchText)
        val address = filter.address
        if (!address.isNullOrEmpty()) put(FILTER_ADDRESS, address)
        val name = filter.name
        if (!name.isNullOrEmpty()) put(FILTER_NAME, name)
        val inn = filter.inn
        if (!inn.isNullOrEmpty()) put(FILTER_INN, inn)
        val type = filter.type
        if (!type.isNullOrEmpty()) put(FILTER_TYPE, type)
        val subscription = filter.subscription
        if (subscription != null) put(FILTER_SUBSCRIPTION, subscription)
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
    }
}