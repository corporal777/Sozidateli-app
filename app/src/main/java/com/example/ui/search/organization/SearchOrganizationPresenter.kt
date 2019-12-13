package com.example.ui.search.organization

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Organization
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
) : SearchPresenter<SearchOrganizationContract.View, Organization, SearchFilter.Organization>(), SearchOrganizationContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        organizationRepository.getOrganizations(limit, offset, buildFilter())
    }

    override fun onOrganizationClick(organization: Organization) {
        viewState.showOrganization(organization)
    }

    override fun onOrganizationSubscriptionClick(organization: Organization) {
        val isSubscribed = organization.isSubscribed ?: false
        val request = if (isSubscribed) organizationRepository.unsubscribe(organization.id)
        else organizationRepository.subscribe(organization.id)

        compositeDisposable += request.performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    organization.isSubscribed = !isSubscribed
                    viewState.changeSubscription(organization)
                }
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

    override fun createFilter() = SearchFilter.Organization()
    override fun copyFilter(filter: SearchFilter.Organization) = filter.copy()

    companion object {
        private const val FILTER_CONTENT = "content"
        private const val FILTER_ADDRESS = "address"
        private const val FILTER_NAME = "name"
        private const val FILTER_INN = "ogrn_inn"
        private const val FILTER_TYPE = "type"
        private const val FILTER_SUBSCRIPTION = "is_subscribed"
    }
}