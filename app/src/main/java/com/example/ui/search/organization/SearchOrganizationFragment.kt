package com.example.ui.search.organization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.databinding.LayoutFilterOrganizationSearchBinding
import com.example.extensions.findItemBy
import com.example.holders.OrganizationItem
import com.example.holders.PlaceholderItem
import com.example.ui.search.SearchFragment
import com.xwray.groupie.Group
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchOrganizationFragment : SearchFragment<SearchOrganizationPresenter, OrganizationNew, SearchFilter.Organization>(), SearchOrganizationContract.View {

    @InjectPresenter
    override lateinit var searchPresenter: SearchOrganizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchOrganizationPresenter>


    @ProvidePresenter
    fun providePresenter(): SearchOrganizationPresenter = presenterProvider.get()

    override fun showOrganization(organization: OrganizationNew) {
        findNavController().navigate(R.id.organization_fragment_new, bundleOf("organizationId" to organization.id.toString()))
    }

    override fun changeSubscription(organization: OrganizationNew) {
        val idLong = organization.id?.toLong()
        adapter.findItemBy { it: OrganizationItem -> it.id == idLong }?.notifyChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun createItem(itemData: OrganizationNew?): Group {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.ORGANIZATION)
        else {
            OrganizationItem(
                itemData,
                { searchPresenter.onOrganizationClick(itemData) },
                { searchPresenter.onOrganizationSubscriptionClick(itemData) }
            )
        }
    }

    override fun createFilterView(filter: SearchFilter.Organization): View {
        return LayoutFilterOrganizationSearchBinding.inflate(LayoutInflater.from(requireContext()), null, false).apply {
//            etAddress.apply {
//                setTextWithoutSearch(filter.address)
//                com.example.extensions.onTextChanged {
//                    filter.address = it.toString()
//                    if (filter.address.isNullOrBlank()) filter.setAddressFilter(null)
//                }
//                onDataSelectedListener = { filter.setAddressFilter(it) }
//            }

            initRegions(filter, tvRegion, tilRegion, tilTown)
            initTowns(filter, tvTown, tilTown)

            initTextFilter(etOrganizationName, filter.name) { filter.name = it }
            initTextFilter(etInn, filter.inn) { filter.inn = it }
        }.root
    }

    override fun clearFilterView(filterView: View) {
        LayoutFilterOrganizationSearchBinding.bind(filterView).apply {
            tvRegion.text = null
            tvTown.text = null

            etOrganizationName.text = null
            etInn.text = null
        }
    }


}